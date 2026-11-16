package com.horizen.economy.database;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.sql.*;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** Single serialized SQLite connection. All mutations are transactional and guarded. */
public final class Database implements AutoCloseable {
    private final Connection connection;
    public Database(JavaPlugin plugin) throws Exception {
        File file = new File(plugin.getDataFolder(), "economy.db");
        file.getParentFile().mkdirs();
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        try (Statement s = connection.createStatement()) {
            s.execute("PRAGMA journal_mode=WAL"); s.execute("PRAGMA foreign_keys=ON"); s.execute("PRAGMA busy_timeout=5000");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS wallets(uuid TEXT PRIMARY KEY,name TEXT NOT NULL,balance INTEGER NOT NULL DEFAULT 0 CHECK(balance>=0))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS bank(uuid TEXT PRIMARY KEY,balance INTEGER NOT NULL DEFAULT 0 CHECK(balance>=0))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS transactions(id INTEGER PRIMARY KEY AUTOINCREMENT,uuid TEXT NOT NULL,kind TEXT NOT NULL,amount INTEGER NOT NULL,details TEXT,created_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS managers(uuid TEXT PRIMARY KEY,purchased_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS unlocks(uuid TEXT NOT NULL,market TEXT NOT NULL,PRIMARY KEY(uuid,market))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS upgrades(uuid TEXT NOT NULL,kind TEXT NOT NULL,level INTEGER NOT NULL DEFAULT 0,PRIMARY KEY(uuid,kind))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS orders(id TEXT PRIMARY KEY,owner TEXT NOT NULL,material TEXT NOT NULL,amount INTEGER NOT NULL,remaining INTEGER NOT NULL,unit_price INTEGER NOT NULL,escrow INTEGER NOT NULL,state TEXT NOT NULL,created_at INTEGER NOT NULL,expires_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS order_history(id INTEGER PRIMARY KEY AUTOINCREMENT,order_id TEXT NOT NULL,owner TEXT NOT NULL,counterparty TEXT NOT NULL,quantity INTEGER NOT NULL,total INTEGER NOT NULL,created_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS auctions(id TEXT PRIMARY KEY,seller TEXT NOT NULL,material TEXT NOT NULL,amount INTEGER NOT NULL,item_blob TEXT NOT NULL,current_bid INTEGER NOT NULL,bidder TEXT,state TEXT NOT NULL,created_at INTEGER NOT NULL,expires_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS auction_bids(id INTEGER PRIMARY KEY AUTOINCREMENT,auction_id TEXT NOT NULL,bidder TEXT NOT NULL,amount INTEGER NOT NULL,created_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS homes(uuid TEXT NOT NULL,name TEXT NOT NULL,world TEXT NOT NULL,x REAL NOT NULL,y REAL NOT NULL,z REAL NOT NULL,yaw REAL NOT NULL,pitch REAL NOT NULL,PRIMARY KEY(uuid,name))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS economy_meta(key TEXT PRIMARY KEY,value TEXT NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS player_storage(uuid TEXT PRIMARY KEY,contents TEXT NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS mailbox(id INTEGER PRIMARY KEY AUTOINCREMENT,uuid TEXT NOT NULL,kind TEXT NOT NULL,payload TEXT NOT NULL,created_at INTEGER NOT NULL,claimed INTEGER NOT NULL DEFAULT 0)");
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_wallet_balance ON wallets(balance DESC)");
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_orders_state ON orders(state,expires_at)");
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_auctions_state ON auctions(state,expires_at)");
        }
    }
    public synchronized <T> T tx(SqlWork<T> work) throws SQLException {
        boolean old = connection.getAutoCommit(); connection.setAutoCommit(false);
        try { T result = work.run(connection); connection.commit(); return result; }
        catch (Exception e) { connection.rollback(); if (e instanceof SQLException se) throw se; throw new SQLException(e); }
        finally { connection.setAutoCommit(old); }
    }
    public synchronized void ensurePlayer(UUID id,String name,long start) throws SQLException {
        try (PreparedStatement p=connection.prepareStatement("INSERT OR IGNORE INTO wallets(uuid,name,balance) VALUES(?,?,?)")) {p.setString(1,id.toString());p.setString(2,name);p.setLong(3,start);p.executeUpdate();}
        try (PreparedStatement p=connection.prepareStatement("UPDATE wallets SET name=? WHERE uuid=?")){p.setString(1,name);p.setString(2,id.toString());p.executeUpdate();}
        try (PreparedStatement p=connection.prepareStatement("INSERT OR IGNORE INTO bank(uuid,balance) VALUES(?,0)")){p.setString(1,id.toString());p.executeUpdate();}
    }
    public synchronized long balance(UUID id) throws SQLException {try(PreparedStatement p=connection.prepareStatement("SELECT balance FROM wallets WHERE uuid=?")){p.setString(1,id.toString());try(ResultSet r=p.executeQuery()){return r.next()?r.getLong(1):0;}}}
    public synchronized long bank(UUID id) throws SQLException {try(PreparedStatement p=connection.prepareStatement("SELECT balance FROM bank WHERE uuid=?")){p.setString(1,id.toString());try(ResultSet r=p.executeQuery()){return r.next()?r.getLong(1):0;}}}
    public synchronized boolean hasManager(UUID id) throws SQLException {try(PreparedStatement p=connection.prepareStatement("SELECT 1 FROM managers WHERE uuid=?")){p.setString(1,id.toString());try(ResultSet r=p.executeQuery()){return r.next();}}}
    public synchronized boolean unlocked(UUID id,String market) throws SQLException {try(PreparedStatement p=connection.prepareStatement("SELECT 1 FROM unlocks WHERE uuid=? AND market=?")){p.setString(1,id.toString());p.setString(2,market);try(ResultSet r=p.executeQuery()){return r.next();}}}
    public synchronized int upgrade(UUID id,String kind) throws SQLException {try(PreparedStatement p=connection.prepareStatement("SELECT level FROM upgrades WHERE uuid=? AND kind=?")){p.setString(1,id.toString());p.setString(2,kind);try(ResultSet r=p.executeQuery()){return r.next()?r.getInt(1):0;}}}
    public synchronized Connection raw(){return connection;}
    public synchronized void backup() throws SQLException {
        try (Statement s=connection.createStatement()) { s.execute("PRAGMA wal_checkpoint(FULL)"); }
        try {
            File source=new File(connection.getMetaData().getURL().replace("jdbc:sqlite:",""));
            File dir=new File(source.getParentFile(),"backups"); if(!dir.exists()&&!dir.mkdirs())throw new IOException("Cannot create backup directory");
            String stamp=DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC).format(Instant.now());
            Files.copy(source.toPath(),new File(dir,"economy-"+stamp+".db").toPath(),StandardCopyOption.REPLACE_EXISTING);
            File[] backups=dir.listFiles((d,n)->n.endsWith(".db")); if(backups!=null&&backups.length>7){Arrays.sort(backups,Comparator.comparingLong(File::lastModified));for(int i=0;i<backups.length-7;i++)Files.deleteIfExists(backups[i].toPath());}
        } catch(IOException e){throw new SQLException("Database backup failed",e);}
    }
    @Override public synchronized void close() throws SQLException { if(connection!=null&&!connection.isClosed()) connection.close(); }
    @FunctionalInterface public interface SqlWork<T>{T run(Connection c)throws Exception;}
}
