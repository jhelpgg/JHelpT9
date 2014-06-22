package jhelp.t9.dictionary;

import jhelp.util.Utilities;

/**
 * Represents a word, its frequency, its exact complete key codes (to type it) and its database ID
 * 
 * @author JHelp
 */
class Word
{
   /**
    * Create a word
    * 
    * @param databaseID
    *           Database ID
    * @param frequency
    *           Frequency
    * @param word
    *           Word
    * @param keys
    *           Reference keys for T9
    * @return Created word
    */
   static Word createWord(final int databaseID, final long frequency, final String word, final String[] keys)
   {
      final char[] characters = word.trim().toCharArray();
      final int length = characters.length;

      if(length == 0)
      {
         return null;
      }

      final int[] keyCode = new int[length];
      char c;
      final int nb = keys.length;

      for(int i = 0; i < length; i++)
      {
         c = characters[i];

         for(int k = 0; k < nb; k++)
         {
            if(keys[k].indexOf(c) >= 0)
            {
               keyCode[i] = k;
               break;
            }
         }
      }

      return new Word(databaseID, Math.max(1L, frequency), word, keyCode);
   }

   /**
    * Create a word, not already in database
    * 
    * @param frequency
    *           Frequency
    * @param word
    *           Word
    * @param keys
    *           References keys in T9
    * @return Word created
    */
   static Word createWord(final long frequency, final String word, final String[] keys)
   {
      return Word.createWord(-1, frequency, word, keys);
   }

   /**
    * Create a word, not already in database
    * 
    * @param word
    *           Word
    * @param keys
    *           References keys in T9
    * @return Word created
    */
   static Word createWord(final String word, final String[] keys)
   {
      return Word.createWord(1, word, keys);
   }

   /** Database ID */
   private int          databaseID;
   /** Frequency */
   private long         frequency;
   /** Exact complete key codes (to type it) */
   private final int[]  keyCode;
   /** Last computed distance */
   private int          lastComputedDistance;
   /** Embed word */
   private final String word;

   /**
    * Create a new instance of Word
    * 
    * @param databaseID
    *           Database ID, -1 if unknown
    * @param frequency
    *           Frequency, at least 1
    * @param word
    *           Word itself
    * @param keyCode
    *           Exact complete key codes (to type it)
    */
   private Word(final int databaseID, final long frequency, final String word, final int[] keyCode)
   {
      this.lastComputedDistance = -1;
      this.databaseID = databaseID;
      this.frequency = frequency;
      this.word = word;
      this.keyCode = keyCode;
   }

   /**
    * Compute distance between word and list of key codes typed.<br>
    * For compute an other distance with a different key code list, first call {@link #reinitializeDistance()}.<br>
    * In fact we store the last result in memory, to give it next time, so reset need to compute new value.<br>
    * The aim is to be fast when search in suggestions, to not compute the same distance several times.
    * 
    * @param codes
    *           List of key codes typed
    * @return Computed distance (Or previous value, if their one)
    */
   public int distance(final int... codes)
   {
      if(this.lastComputedDistance >= 0)
      {
         return this.lastComputedDistance;
      }

      final int codesLength = codes.length;
      final int thisLength = this.keyCode.length;
      int distance = 0;
      int more = 1;
      int indexCodes = 0;
      int indexThis = 0;

      while((indexCodes < codesLength) && (indexThis < thisLength))
      {
         if((indexCodes >= codesLength) || (indexThis >= thisLength) || (this.keyCode[indexThis] != codes[indexCodes]))
         {
            distance += more;
            more++;

            if(indexThis < thisLength)
            {
               indexThis++;
            }
            else
            {
               distance += more;
               more++;
               indexCodes++;
            }
         }
         else
         {
            indexCodes++;
            indexThis++;
         }
      }

      distance += codesLength - indexCodes;
      this.lastComputedDistance = distance;
      return distance;
   }

   /**
    * Database ID
    * 
    * @return Database ID or -1 if not in database
    */
   public int getDatabaseID()
   {
      return this.databaseID;
   }

   /**
    * Word frequency
    * 
    * @return Word frequency
    */
   public long getFrequency()
   {
      return this.frequency;
   }

   /**
    * Exact complete key codes (to type it)
    * 
    * @return Exact complete key codes (to type it)
    */
   public int[] getKeyCode()
   {
      return Utilities.createCopy(this.keyCode);
   }

   /**
    * Last computed distance
    * 
    * @return Last computed distance or -1, if no previous computed
    */
   public int getLastComputedDistance()
   {
      return this.lastComputedDistance;
   }

   /**
    * Embed word
    * 
    * @return Embed word
    */
   public String getWord()
   {
      return this.word;
   }

   /**
    * Increment word frequency
    * 
    * @param weight
    *           Weight to add
    */
   public void incrementFrequency(final long weight)
   {
      this.frequency += weight;
   }

   /**
    * Put last distance to not initialized to allow to compute new one again
    */
   public void reinitializeDistance()
   {
      this.lastComputedDistance = -1;
   }

   /**
    * Define database ID, if word haven't already a database ID
    * 
    * @param databaseID
    *           Database ID to set
    */
   public void setDatabaseID(final int databaseID)
   {
      if(this.databaseID >= 0)
      {
         return;
      }

      this.databaseID = databaseID;
   }

   /**
    * String representation <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @return String representation
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.word + ":" + this.frequency + " ID=" + this.databaseID + " distance=" + this.lastComputedDistance;
   }
}