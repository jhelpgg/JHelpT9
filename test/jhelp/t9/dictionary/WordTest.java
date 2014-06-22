package jhelp.t9.dictionary;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Word}
 * 
 * @author JHelp
 */
public class WordTest
{
   /** T9 keys */
   private static final String[] KEYS =
                                      {
         "@- 0", ".,;:!?'1", "aàääbc2ABC", "deéèêëf3DEF", "ghiîï4GHIJ", "jkl5JKL", "mnoôö6MNO", "pqrs7PQRS", "tuùûüv8TUV", "wxyz9WXYZ"

                                      };

   /**
    * Distance test
    */
   @Test
   public void testDistance()
   {
      final Word first = Word.createWord(3050976L, "mise au point", WordTest.KEYS);
      first.reinitializeDistance();
      final int distanceFirst = first.distance(6, 4, 7, 3);

      final Word second = Word.createWord(1614609L, "égrenoir", WordTest.KEYS);
      second.reinitializeDistance();
      final int distanceSecond = second.distance(6, 4, 7, 3);

      Assert.assertTrue("mise=" + distanceFirst + " égrenoir=" + distanceSecond, distanceFirst < distanceSecond);

      final WordComparator wordComparator = new WordComparator(6, 4, 7, 3);
      first.reinitializeDistance();
      second.reinitializeDistance();
      Assert.assertTrue(wordComparator.compare(first, second) < 0);
   }
}