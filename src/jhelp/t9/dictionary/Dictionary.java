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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jhelp.database.ColumnDescription;
import jhelp.database.Condition;
import jhelp.database.ConditionColumnEquals;
import jhelp.database.Data;
import jhelp.database.DataType;
import jhelp.database.Database;
import jhelp.database.DatabaseException;
import jhelp.database.QueryResult;
import jhelp.database.SelectQuery;
import jhelp.database.TableInfo;
import jhelp.database.UpdateQuery;
import jhelp.database.Value;
import jhelp.t9.ResourcesT9;
import jhelp.util.io.UtilIO;

/**
 * A dictionary for T9.<br>
 * T9 have only 10 key, with each a list of characters they represents.<br>
 * <br>
 * The very first usage, or reset are long operation, because they recreate all database (several minutes in my computer). But
 * when database is created, t9 copied, (next times in fact), dictionary initialization, take only 1 second or less.<br>
 * The suggestion list is size limited, because it is useless to give a to big list to user and for compute speed reason. Limits
 * are in [{@link #MINIUM_SUGGESTION_LIST_SIZE}, {@link #MAXIUM_SUGGESTION_LIST_SIZE}]<br>
 * <b>WARNING !</b> Don't forget to close properly the dictionary with {@link #close()} method, to be sure last modification are
 * correctly commit inside dictionary database
 * 
 * @author JHelp
 */
public class Dictionary
{
   /** Additional list name */
   private static final String            ADDITIONAL_WORD_LIST        = "additionalList.txt";
   /** Directory where read/write dictionary database and key map */
   private static final File              BASE_DIRECTORY              = UtilIO.obtainExternalFile("JHelp/T9/Dictionary");
   /** Frequency column in {@link #TABLE_WORDS} table */
   private static final ColumnDescription COLUMN_FRENQUENCY;
   /** Frequency column name in {@link #TABLE_WORDS} table */
   private static final String            COLUMN_FREQUENCY_NAME;
   /** Word column name in {@link #TABLE_WORDS} table */
   private static final String            COLUMN_WORD_NAME;
   /** Word column in {@link #TABLE_WORDS} table */
   private static final ColumnDescription COLUMN_WORDS;
   /** Frequency reference in {@link #GRAMMAR} */
   private static final String            FREQUENCY                   = "<frequency>";
   /** Declare a new grammar to use when parsing {@link #INITIAL_WORD_LIST} */
   private static final String            GRAMMAR                     = "$GRAMMAR$";
   /** Initial word, frequency list resource name */
   private static final String            INITIAL_WORD_LIST           = "initialWordList.txt";
   /** Query to select all words in database */
   private static final SelectQuery       SELECT_OBTAIN_ALL_WORDS;
   /** Index of column frequency in result of {@link #SELECT_OBTAIN_ALL_WORDS} */
   private static final int               SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_FREQUENCY;
   /** Index of column ID in result of {@link #SELECT_OBTAIN_ALL_WORDS} */
   private static final int               SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_ID;
   /** Index of column word in result of {@link #SELECT_OBTAIN_ALL_WORDS} */
   private static final int               SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_WORDS;
   /** Space or tabulation separator reference in {@link #GRAMMAR} */
   private static final String            SPACE                       = "[SPACE]";
   /** T9 map key resource name */
   private static final String            T9_MAP_NAME                 = "t9.xml";
   /** Table of words description */
   private static final TableInfo         TABLE_WORDS;
   /** Table of words */
   private static final String            TABLE_WORDS_NAME;
   /** Query for change word frequency */
   private static final UpdateQuery       UPDATE_WORD_FREQUENCY;
   /** Reference to frequency in {@link #UPDATE_WORD_FREQUENCY} */
   private static final Data              UPDATE_WORD_FREQUENCY_DATA_FREQUENCY;
   /** Value for frequency in {@link #UPDATE_WORD_FREQUENCY} */
   private static final Value             UPDATE_WORD_FREQUENCY_VALUE_FREQUENCY;
   /** Reference to ID in {@link #UPDATE_WORD_FREQUENCY} */
   private static final Data              UPDATE_WORD_FREQUENCY_WHERE_DATA_ID;
   /** Where condition of {@link #UPDATE_WORD_FREQUENCY} */
   private static final Condition         UPDATE_WORD_FREQUENCY_WHERE_ID_IS;
   /** Word reference in {@link #GRAMMAR} */
   private static final String            WORD                        = "<word>";
   /** Word list file name (Database file name) */
   private static final String            WORD_LIST_NAME              = "wordList";
   /**
    * Maximum suggestion list size. Put this value to big is useless for user (To much choice, kill the choice). And if this
    * value is to big it will improve {@link #computeSuggestion(int, int...)} performance
    */
   public static final int                MAXIUM_SUGGESTION_LIST_SIZE = 20;
   /**
    * Minimum suggestion list size. Put this value under 1 is no sense, why compute a list of suggestion to have an empty list ?
    */
   public static final int                MINIUM_SUGGESTION_LIST_SIZE = 5;

   static
   {
      // Table WORDS
      TABLE_WORDS_NAME = "WORDS";
      COLUMN_WORD_NAME = "Word";
      COLUMN_FREQUENCY_NAME = "Frequency";
      COLUMN_WORDS = new ColumnDescription(Dictionary.COLUMN_WORD_NAME, DataType.LONGVARCHAR);
      COLUMN_FRENQUENCY = new ColumnDescription(Dictionary.COLUMN_FREQUENCY_NAME, DataType.BIGINT);
      TABLE_WORDS = new TableInfo(Dictionary.TABLE_WORDS_NAME, Database.COLUMN_ID, Dictionary.COLUMN_WORDS, Dictionary.COLUMN_FRENQUENCY);

      // Select all words
      SELECT_OBTAIN_ALL_WORDS = new SelectQuery(Dictionary.TABLE_WORDS);
      SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_ID = 0;
      SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_WORDS = 1;
      SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_FREQUENCY = 2;

      // Update word frequency
      UPDATE_WORD_FREQUENCY_WHERE_DATA_ID = new Data(0);
      UPDATE_WORD_FREQUENCY_WHERE_ID_IS = new ConditionColumnEquals(Database.ID, Dictionary.UPDATE_WORD_FREQUENCY_WHERE_DATA_ID);
      UPDATE_WORD_FREQUENCY_DATA_FREQUENCY = new Data(0L);
      UPDATE_WORD_FREQUENCY_VALUE_FREQUENCY = new Value(Dictionary.COLUMN_FREQUENCY_NAME, Dictionary.UPDATE_WORD_FREQUENCY_DATA_FREQUENCY);
      UPDATE_WORD_FREQUENCY = new UpdateQuery(Dictionary.TABLE_WORDS_NAME, Dictionary.UPDATE_WORD_FREQUENCY_VALUE_FREQUENCY);
      Dictionary.UPDATE_WORD_FREQUENCY.setWhere(Dictionary.UPDATE_WORD_FREQUENCY_WHERE_ID_IS);
   }

   /** T9 key map */
   private final String[]                 keys;
   /** Dictionary language */
   private final String                   language;
   /** Directory where found T9 map and database for the language of this dictonary */
   private final File                     languageDirectory;
   /** Characters considered as separators */
   private String                         separators;
   /** Database of words */
   private Database                       wordListBase;
   /** Database file */
   private final File                     wordListFile;
   /** Words in RAM */
   private final HashMap<String, Word>    words;

   /**
    * Create a new instance of Dictionary
    * 
    * @param language
    *           Dictionary language
    * @throws DictionaryException
    *            If language dosen't exists (neither "next" the jar, neither inside resource) or issue on reading/writing
    *            database
    */
   public Dictionary(final String language)
         throws DictionaryException
   {
      if(language == null)
      {
         throw new NullPointerException("language musn't be null");
      }

      this.language = language;
      this.languageDirectory = new File(Dictionary.BASE_DIRECTORY, language);
      this.keys = new String[10];
      this.words = new HashMap<String, Word>();
      this.wordListFile = new File(this.languageDirectory, Dictionary.WORD_LIST_NAME);

      this.initializeDictionary(false);
   }

   /**
    * Add additional list word
    * 
    * @throws DictionaryException
    *            On database access issue OR on reading resource issue (doesn't exists, ...)
    */
   private void addAdditionalList() throws DictionaryException
   {
      final InputStream inputStream = ResourcesT9.RESOURCES_T9.obtainResourceStream("dico/" + this.language + "/" + Dictionary.ADDITIONAL_WORD_LIST);
      BufferedReader bufferedReader = null;
      try
      {
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         String line = bufferedReader.readLine();

         while(line != null)
         {
            line = line.trim();

            if(line.length() > 0)
            {
               this.addWord(line);
            }

            line = bufferedReader.readLine();
         }
      }
      catch(final Exception exception)
      {
         throw new DictionaryException(exception, "Failed to add additional list for ", this.language);
      }
      finally
      {
         if(bufferedReader != null)
         {
            try
            {
               bufferedReader.close();
            }
            catch(final Exception exception)
            {
            }
         }
      }

   }

   /**
    * Initialize the dictionary
    * 
    * @param reset
    *           Indicates if have to for a complete reset of database to original values (May take several minutes)
    * @throws DictionaryException
    *            On database access issue OR on writing (database, t9 map) issue OR on reading resources issue
    */
   private void initializeDictionary(boolean reset) throws DictionaryException
   {
      if(reset == true)
      {
         UtilIO.delete(this.languageDirectory);
      }

      // Get map
      final File map = new File(this.languageDirectory, Dictionary.T9_MAP_NAME);

      if(map.exists() == false)
      {
         reset = true;

         final InputStream inputStream = ResourcesT9.RESOURCES_T9.obtainResourceStream("dico/" + this.language + "/" + Dictionary.T9_MAP_NAME);

         if(inputStream == null)
         {
            throw new DictionaryException("The map ", map.getAbsolutePath(), " dosen't exists, please create it before use the language ", this.language);
         }

         try
         {
            UtilIO.write(inputStream, map);
         }
         catch(final Exception exception)
         {
            throw new DictionaryException(exception, "Failed to get map for ", this.language, " from embed resource and/or copying it in ",
                  map.getAbsolutePath());
         }
         finally
         {
            try
            {
               inputStream.close();
            }
            catch(final Exception exception)
            {
            }
         }
      }

      // Parse map
      InputStream inputStream = null;

      try
      {
         inputStream = new FileInputStream(map);
         final MapParser mapParser = new MapParser(inputStream, this.keys);
         this.separators = mapParser.getSeparators();
      }
      catch(final Exception exception)
      {
         throw new DictionaryException(exception, "Failed to parse map for ", this.language, " at ", map.getAbsolutePath());
      }
      finally
      {
         if(inputStream != null)
         {
            try
            {
               inputStream.close();
            }
            catch(final Exception exception)
            {
            }
         }
      }

      // Obtain wordList
      this.words.clear();

      try
      {
         if(this.wordListBase == null)
         {
            this.wordListBase = new Database(this.wordListFile.getAbsolutePath());
         }

         this.wordListBase.createTable(Dictionary.TABLE_WORDS);

         // Fill words
         final QueryResult queryResult = this.wordListBase.query(Dictionary.SELECT_OBTAIN_ALL_WORDS);
         final int numberOrWords = queryResult.numberOfRows();
         int databaseID;
         String string;
         long frequency;
         Word word;

         for(int row = 0; row < numberOrWords; row++)
         {
            databaseID = queryResult.getData(Dictionary.SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_ID, row).getInt();
            string = queryResult.getData(Dictionary.SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_WORDS, row).getLongString();
            frequency = queryResult.getData(Dictionary.SELECT_OBTAIN_ALL_WORDS_INDEX_COLUMUN_FREQUENCY, row).getLong();
            word = Word.createWord(databaseID, frequency, string, this.keys);
            this.words.put(string, word);
         }

         queryResult.destroy();
      }
      catch(final Exception exception)
      {
         throw new DictionaryException(exception, "Failed to create or access to database ", this.wordListFile.getAbsolutePath());
      }

      if(reset == true)
      {
         this.insertInitialList(this.language);
         this.addAdditionalList();
      }
   }

   /**
    * Insert initial file word, frequency list in database
    * 
    * @param language
    *           Dictionary language
    * @throws DictionaryException
    *            On database access issue, OR on reading resource issue
    */
   private void insertInitialList(final String language) throws DictionaryException
   {
      final InputStream inputStream = ResourcesT9.RESOURCES_T9.obtainResourceStream("dico/" + language + "/" + Dictionary.INITIAL_WORD_LIST);
      BufferedReader bufferedReader = null;
      try
      {
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         String line = bufferedReader.readLine();
         boolean frequencyFirst = true;
         String separator = Dictionary.SPACE;
         int indexFrequency;
         int indexWord;
         int index, index2, start, end;
         final String[] read = new String[2];
         char[] characters;

         while(line != null)
         {
            line = line.trim();

            if((line.length() > 0) && (line.charAt(0) != '#'))
            {
               if(line.startsWith(Dictionary.GRAMMAR) == true)
               {
                  line = line.substring(Dictionary.GRAMMAR.length()).trim();
                  indexFrequency = line.indexOf(Dictionary.FREQUENCY);
                  indexWord = line.indexOf(Dictionary.WORD);

                  if((indexFrequency >= 0) && (indexWord >= 0))
                  {
                     if(indexFrequency < indexWord)
                     {
                        index = indexFrequency + Dictionary.FREQUENCY.length();

                        if(index < indexWord)
                        {
                           separator = line.substring(index, indexWord);
                           frequencyFirst = true;
                        }
                     }
                     else
                     {
                        index = indexWord + Dictionary.WORD.length();

                        if(index < indexFrequency)
                        {
                           separator = line.substring(index, indexFrequency);
                           frequencyFirst = false;
                        }
                     }
                  }
               }
               else
               {
                  read[0] = null;
                  read[1] = null;

                  if(Dictionary.SPACE.equals(separator) == true)
                  {
                     index = line.indexOf(' ');
                     index2 = line.indexOf('\t');

                     if((index < 0) || ((index2 >= 0) && (index2 < index)))
                     {
                        start = index2;
                     }
                     else
                     {
                        start = index;
                     }

                     if(start >= 0)
                     {
                        end = start;
                        characters = line.toCharArray();

                        while((end < characters.length) && ((characters[end] == ' ') || (characters[end] == '\t')))
                        {
                           end++;
                        }

                        if(end < characters.length)
                        {
                           read[0] = line.substring(0, start).trim();
                           read[1] = line.substring(end).trim();
                        }
                     }
                  }
                  else
                  {
                     index = line.indexOf(separator);

                     if(index >= 0)
                     {
                        read[0] = line.substring(0, index).trim();
                        read[1] = line.substring(index + 1).trim();
                     }
                  }

                  if(read[0] != null)
                  {
                     if(frequencyFirst == true)
                     {
                        indexFrequency = 0;
                        indexWord = 1;
                     }
                     else
                     {
                        indexFrequency = 1;
                        indexWord = 0;
                     }

                     this.insertWord(Long.parseLong(read[indexFrequency]), read[indexWord]);
                  }
               }
            }

            line = bufferedReader.readLine();
         }
      }
      catch(final Exception exception)
      {
         throw new DictionaryException(exception, "Failed to insert initial list for ", language);
      }
      finally
      {
         if(bufferedReader != null)
         {
            try
            {
               bufferedReader.close();
            }
            catch(final Exception exception)
            {
            }
         }
      }

   }

   /**
    * Insert a word in dictionary
    * 
    * @param frequency
    *           Initial frequency
    * @param string
    *           Word to insert
    * @throws DatabaseException
    *            On database access issue
    */
   private void insertWord(final long frequency, final String string) throws DatabaseException
   {
      final Word word = Word.createWord(frequency, string, this.keys);
      final int databaseID = this.wordListBase.insert(Dictionary.TABLE_WORDS_NAME, //
            new Value(Dictionary.COLUMN_WORD_NAME, Data.createLongStringData(string)),//
            new Value(Dictionary.COLUMN_FREQUENCY_NAME, new Data(word.getFrequency())));
      word.setDatabaseID(databaseID);
      this.words.put(string, word);
   }

   /**
    * Add a word, or increment its frequency, in the dictionary
    * 
    * @param string
    *           Word to add
    * @throws DatabaseException
    *            On database access issue
    */
   public void addWord(final String string) throws DatabaseException
   {
      this.addWord(string, 1L);
   }

   /**
    * Add a word, or increment its frequency, in the dictionary
    * 
    * @param string
    *           Word to add
    * @param weight
    *           Initial frequency or frequency to add for word
    * @throws DatabaseException
    *            On database access issue
    */
   public void addWord(final String string, final long weight) throws DatabaseException
   {
      Word word = this.words.get(string);

      if(word == null)
      {
         word = Word.createWord(Math.max(1L, weight), string, this.keys);

         // Insert word in data base
         final int databaseID = this.wordListBase.insert(Dictionary.TABLE_WORDS_NAME, //
               new Value(Dictionary.COLUMN_WORD_NAME, Data.createLongStringData(string)),//
               new Value(Dictionary.COLUMN_FREQUENCY_NAME, new Data(word.getFrequency())));
         word.setDatabaseID(databaseID);
         this.words.put(string, word);
      }
      else
      {
         word.incrementFrequency(Math.max(1L, weight));

         // Update word frequency in database
         Dictionary.UPDATE_WORD_FREQUENCY_DATA_FREQUENCY.set(word.getFrequency());
         Dictionary.UPDATE_WORD_FREQUENCY_WHERE_DATA_ID.set(word.getDatabaseID());
         this.wordListBase.update(Dictionary.UPDATE_WORD_FREQUENCY);
      }
   }

   /**
    * Close properly the dictionary.<br>
    * Don't forget to call it
    * 
    * @throws DictionaryException
    *            On closing database issue
    */
   public void close() throws DictionaryException
   {
      try
      {
         this.wordListBase.closeDatabase();
      }
      catch(final Exception exception)
      {
         throw new DictionaryException(exception, "Failed to close dictionary (may be already closed)");
      }
   }

   /**
    * Compute list of suggestion from list of key typed
    * 
    * @param limitSize
    *           Number of suggestion in list (10 is a good value)
    * @param codes
    *           List of keys types (All integers MUST be in [0, 9], else may have exception or strange result)
    * @return Suggestion list
    */
   public List<String> computeSuggestion(int limitSize, final int... codes)
   {
      limitSize = Math.max(Dictionary.MINIUM_SUGGESTION_LIST_SIZE, Math.min(Dictionary.MAXIUM_SUGGESTION_LIST_SIZE, limitSize));

      final WordComparator wordComparator = new WordComparator(codes);
      final Word[] words = new Word[limitSize];
      int size = 0;
      int comp;
      int min;
      Word wordActual;
      int max;
      int mil;

      // Zero comparison case will never append, because never the same word 2 times

      for(final Word word : this.words.values())
      {
         // Word distance will be initialize exactly one time, and compute one or zero time
         // This because we never meet same word more than 1 time
         // And when distance is computed, its is memorized
         word.reinitializeDistance();

         if(size == 0)
         {
            words[0] = word;
            size = 1;
            continue;
         }

         min = 0;
         wordActual = words[0];
         comp = wordComparator.compare(word, wordActual);

         if(comp < 0)
         {
            System.arraycopy(words, 0, words, 1, limitSize - 1);
            words[0] = word;
            size = Math.min(size + 1, limitSize);
            continue;
         }

         max = size - 1;
         wordActual = words[max];
         comp = wordComparator.compare(word, wordActual);

         if(comp > 0)
         {
            if(size < limitSize)
            {
               words[size] = word;
               size++;
            }

            continue;
         }

         while((min + 1) < max)
         {
            mil = (min + max) >> 1;

            wordActual = words[mil];
            comp = wordComparator.compare(word, wordActual);

            if(comp < 0)
            {
               max = mil;
            }
            else
            {
               min = mil;
            }
         }

         System.arraycopy(words, max, words, max + 1, limitSize - max - 1);
         words[max] = word;
         size = Math.min(size + 1, limitSize);
      }

      final List<String> list = new ArrayList<String>(limitSize);

      for(final Word word : words)
      {
         list.add(word.getWord());
      }

      return list;
   }

   /**
    * Obtain frequency for a word
    * 
    * @param string
    *           Word search
    * @return Frequency of the word OR -1 if word not in dictionary
    */
   public long getFrequency(final String string)
   {
      final Word word = this.words.get(string);

      if(word == null)
      {
         return -1;
      }

      return word.getFrequency();
   }

   /**
    * List of characters associated to a key
    * 
    * @param key
    *           Key in [0, 9]
    * @return List of characters associated
    */
   public String getKey(final int key)
   {
      return this.keys[key];
   }

   /**
    * Characters considered as separators
    * 
    * @return Characters considered as separators
    */
   public String getSeparators()
   {
      return this.separators;
   }

   /**
    * Reset the dictionary to its original state.<br>
    * May take several minutes
    * 
    * @throws DictionaryException
    *            On reading resources issue OR on writing files issue OR on database access issue
    */
   public void resetDictionary() throws DictionaryException
   {
      this.initializeDictionary(true);
   }
}