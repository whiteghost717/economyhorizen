package com.horizen.economy.economy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MoneyTest {
 @Test void rejectsDecimalAndNegativeAmounts(){assertThrows(IllegalArgumentException.class,()->Money.parse("1.5"));assertThrows(IllegalArgumentException.class,()->Money.parse("-1"));}
 @Test void parsesWholeRupees(){assertEquals(1000000L,Money.parse("1000000"));}
 @Test void formatsCompactBalances(){assertEquals("रू1k",Money.format(1000,true,"रू"));assertEquals("रू1m",Money.format(1_000_000,true,"रू"));assertEquals("रू1b",Money.format(1_000_000_000,true,"रू"));assertEquals("रू1t",Money.format(1_000_000_000_000L,true,"रू"));}
 @Test void formatsFullBalances(){assertEquals("रू1,000",Money.format(1000,false,"रू"));}
}
