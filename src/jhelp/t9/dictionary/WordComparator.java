/**
 * <h1>License :</h1> <br>
 * The following code is deliver as is. I take care that code compile and work, but I am not responsible about any damage it may
 * cause.<br>
 * You can use, modify, the code as your need for any usage. But you can't do any action that avoid me or other person use,
 * modify this code. The code is free for usage and modification, you can't change that fact.<br>
 * <br>
 * 
 * @author JHelp
 */
package jhelp.t9.dictionary;

import java.util.Comparator;

import jhelp.util.math.UtilMath;

/**
 * Compare words, first their distance to list of keys, then by frequency, then alphabetic orders
 * 
 * @author JHelp
 */
public class WordComparator
      implements Comparator<Word>
{
   /** List of keys */
   private final int[] codes;

   /**
    * Create a new instance of WordComparator
    * 
    * @param codes
    *           List of keys;
    */
   public WordComparator(final int... codes)
   {
      if(codes == null)
      {
         throw new NullPointerException("codes musn't be null");
      }

      this.codes = codes;
   }

   /**
    * Compare 2 words.<br>
    * It returns:
    * <table border=0>
    * <tr>
    * <th>&lt;0</th>
    * <td>If first word before second</td>
    * </tr>
    * <tr>
    * <th>0</th>
    * <td>If first and second words are equals</td>
    * </tr>
    * <tr>
    * <th>&gt;0</th>
    * <td>If first word after second</td>
    * </tr>
    * <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param word1
    *           First word
    * @param word2
    *           Second word
    * @return Comparison result
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(final Word word1, final Word word2)
   {
      // Smaller distance first
      int comp = word1.distance(this.codes) - word2.distance(this.codes);

      if(comp != 0)
      {
         return comp;
      }

      // Bigger frequency first
      comp = UtilMath.sign(word2.getFrequency() - word1.getFrequency());

      if(comp != 0)
      {
         return comp;
      }

      // Alphabetic order ignore case
      final String w1 = word1.getWord();
      final String w2 = word2.getWord();
      comp = w1.compareToIgnoreCase(w2);

      if(comp != 0)
      {
         return comp;
      }

      // Alphabetic order case sensitive
      return w1.compareTo(w2);
   }
}