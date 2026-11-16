package com.horizen.economy.economy;
import com.horizen.economy.database.Database;import java.sql.*;import java.util.UUID;
public final class EconomyService {
 private final Database db; private final long maxBalance;
 public EconomyService(Database db,long maxBalance){this.db=db;this.maxBalance=maxBalance;}
 public long balance(UUID id)throws SQLException{return db.balance(id);}
 public void transfer(UUID from,UUID to,String toName,long amount,String kind)throws SQLException{
  if(amount<=0)throw new IllegalArgumentException("Amount must be positive."); if(from.equals(to))throw new IllegalArgumentException("You cannot pay yourself.");
  db.tx(c->{try(PreparedStatement p=c.prepareStatement("INSERT OR IGNORE INTO wallets(uuid,name,balance) VALUES(?,?,0)")){p.setString(1,to.toString());p.setString(2,toName);p.executeUpdate();}
   try(PreparedStatement p=c.prepareStatement("UPDATE wallets SET balance=balance-? WHERE uuid=? AND balance>=?")){p.setLong(1,amount);p.setString(2,from.toString());p.setLong(3,amount);if(p.executeUpdate()!=1)throw new IllegalStateException("Insufficient funds.");}
   try(PreparedStatement p=c.prepareStatement("UPDATE wallets SET balance=balance+? WHERE uuid=? AND balance<=?")){p.setLong(1,amount);p.setString(2,to.toString());p.setLong(3,maxBalance-amount);if(p.executeUpdate()!=1)throw new IllegalStateException("Recipient balance limit reached.");}
   log(c,from,kind,-amount,"to="+to);log(c,to,kind,amount,"from="+from);return null;});
 }
 public void adjust(UUID id,String name,long amount,String kind)throws SQLException{db.tx(c->{try(PreparedStatement p=c.prepareStatement("INSERT OR IGNORE INTO wallets(uuid,name,balance) VALUES(?,?,0)")){p.setString(1,id.toString());p.setString(2,name);p.executeUpdate();}
  if(amount>=0){try(PreparedStatement p=c.prepareStatement("UPDATE wallets SET balance=balance+? WHERE uuid=? AND balance<=?")){p.setLong(1,amount);p.setString(2,id.toString());p.setLong(3,maxBalance-amount);if(p.executeUpdate()!=1)throw new IllegalStateException("Balance limit reached.");}}
  else{try(PreparedStatement p=c.prepareStatement("UPDATE wallets SET balance=balance-? WHERE uuid=? AND balance>=?")){p.setLong(1,-amount);p.setString(2,id.toString());p.setLong(3,-amount);if(p.executeUpdate()!=1)throw new IllegalStateException("Insufficient funds.");}}
  log(c,id,kind,amount,"admin adjustment");return null;});}
 public void set(UUID id,String name,long value)throws SQLException{if(value<0||value>maxBalance)throw new IllegalArgumentException("Invalid balance.");db.tx(c->{try(PreparedStatement p=c.prepareStatement("INSERT INTO wallets(uuid,name,balance) VALUES(?,?,?) ON CONFLICT(uuid) DO UPDATE SET name=excluded.name,balance=excluded.balance")){p.setString(1,id.toString());p.setString(2,name);p.setLong(3,value);p.executeUpdate();}log(c,id,"SET",value,"admin set");return null;});}
 public static void log(Connection c,UUID id,String kind,long amount,String details)throws SQLException{try(PreparedStatement p=c.prepareStatement("INSERT INTO transactions(uuid,kind,amount,details,created_at) VALUES(?,?,?,?,?)")){p.setString(1,id.toString());p.setString(2,kind);p.setLong(3,amount);p.setString(4,details);p.setLong(5,System.currentTimeMillis());p.executeUpdate();}}
}
