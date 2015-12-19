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
package jhelp.t9.ui;

import java.util.List;

import jhelp.database.DatabaseException;
import jhelp.gui.twoD.JHelpActionListener;
import jhelp.gui.twoD.JHelpBackgroundRoundRectangle;
import jhelp.gui.twoD.JHelpButtonBehavior;
import jhelp.gui.twoD.JHelpComponent2D;
import jhelp.gui.twoD.JHelpEditText;
import jhelp.gui.twoD.JHelpFrame2D;
import jhelp.gui.twoD.JHelpLabelText2D;
import jhelp.gui.twoD.JHelpTableLayout;
import jhelp.gui.twoD.JHelpTableLayout.JHelpTableLayoutConstraints;
import jhelp.t9.dictionary.Dictionary;
import jhelp.t9.dictionary.DictionaryException;
import jhelp.util.debug.Debug;
import jhelp.util.gui.JHelpFont;
import jhelp.util.list.ArrayInt;

/**
 * Frame sample for show T9 in action.<br>
 * When user choose key, list of suggestion dynamically modified.<br>
 * Choose a word in suggestions, make it selected and increase its usage frequency.<br>
 * Add new world is possible.<br>
 * Choose something in suggestion and add word modify the dictionary.<br>
 * So it can be use to make dictionary more accurate
 * 
 * @author JHelp
 */
public class FrameT9
      extends JHelpFrame2D
{
   /**
    * React to user events
    * 
    * @author JHelp
    */
   class EventManager
         implements JHelpActionListener
   {
      /**
       * Create a new instance of EventManager
       */
      EventManager()
      {
      }

      /**
       * Called when one of button is pressed <br>
       * <br>
       * <b>Parent documentation:</b><br>
       * {@inheritDoc}
       * 
       * @param component2d
       *           Component pressed
       * @param identifier
       *           Button identifier
       * @see jhelp.gui.twoD.JHelpActionListener#actionAppend(jhelp.gui.twoD.JHelpComponent2D, int)
       */
      @Override
      public void actionAppend(final JHelpComponent2D component2d, final int identifier)
      {
         FrameT9.this.keyPress(identifier);
      }
   }

   /** Add a world action */
   private static final int         ACTION_ADD_WORD       = -1;
   /** Clear types letters action */
   private static final int         ACTION_CLEAR          = -2;
   /** Font used */
   private static final JHelpFont   NUMBER                = new JHelpFont("Arial", 24, true);
   /** Number of suggestions */
   private static final int         NUMBER_OF_SUGGESTIONS = 10;
   /** Actual types keys */
   private final ArrayInt           arrayInt;
   /** Dictionary used */
   private final Dictionary         dictionary;
   /** Edit text for type new word */
   private final JHelpEditText      editText;
   /** User events manager */
   private final EventManager       eventManager;
   /** Suggestion list */
   private final JHelpLabelText2D[] suggestions;
   /** Result word or part of word */
   private final JHelpLabelText2D   textTyped;

   /**
    * Create a new instance of FrameT9
    * 
    * @param dictionary
    *           Dictionary to use
    */
   public FrameT9(final Dictionary dictionary)
   {
      super("T9", new JHelpTableLayout());

      if(dictionary == null)
      {
         throw new NullPointerException("dictionary musn't be null");
      }

      this.setExitAllOnClose(true);

      this.dictionary = dictionary;
      this.eventManager = new EventManager();
      this.textTyped = new JHelpLabelText2D(FrameT9.NUMBER, "");
      this.arrayInt = new ArrayInt();
      JHelpTableLayoutConstraints constraints = new JHelpTableLayoutConstraints(0, 0, 9, 1);
      this.addComponent2D(this.textTyped, constraints);
      int key = 1;

      for(int y = 0; y < 3; y++)
      {
         for(int x = 0; x < 3; x++)
         {
            this.addKey(key, x * 3, (y * 3) + 1);
            key++;
         }
      }

      this.addKey(0, 3, 10);

      JHelpLabelText2D text2d = new JHelpLabelText2D(FrameT9.NUMBER, "CLEAR");
      constraints = new JHelpTableLayoutConstraints(0, 10, 3, 3);
      this.addComponent2D(text2d, constraints);
      JHelpButtonBehavior.giveButtonBehavior(FrameT9.ACTION_CLEAR, text2d, this.eventManager);

      int y = 13;
      this.suggestions = new JHelpLabelText2D[FrameT9.NUMBER_OF_SUGGESTIONS];

      for(int i = 0; i < FrameT9.NUMBER_OF_SUGGESTIONS; i++)
      {
         constraints = new JHelpTableLayoutConstraints(0, y, 9, 1);
         y++;
         this.suggestions[i] = new JHelpLabelText2D(FrameT9.NUMBER, "");
         this.addComponent2D(this.suggestions[i], constraints);
         JHelpButtonBehavior.giveButtonBehavior(100 + i, this.suggestions[i], this.eventManager);
      }

      this.editText = new JHelpEditText(FrameT9.NUMBER, 30);
      constraints = new JHelpTableLayoutConstraints(0, y, 8, 1);
      this.addComponent2D(this.editText, constraints);

      text2d = new JHelpLabelText2D(FrameT9.NUMBER, "ADD");
      constraints = new JHelpTableLayoutConstraints(8, y, 1, 1);
      this.addComponent2D(text2d, constraints);
      JHelpButtonBehavior.giveButtonBehavior(FrameT9.ACTION_ADD_WORD, text2d, this.eventManager);
   }

   /**
    * Add a key button
    * 
    * @param key
    *           Key ID
    * @param x
    *           X
    * @param y
    *           Y
    */
   private void addKey(final int key, final int x, final int y)
   {
      final JHelpTableLayoutConstraints constraints = new JHelpTableLayoutConstraints(x, y, 3, 3);

      final JHelpLabelText2D labelText2D = new JHelpLabelText2D(FrameT9.NUMBER, String.valueOf(key) + "\n" + this.dictionary.getKey(key));
      JHelpButtonBehavior.giveButtonBehavior(key, labelText2D, this.eventManager);

      final JHelpBackgroundRoundRectangle backgroundRoundRectangle = new JHelpBackgroundRoundRectangle(labelText2D);
      backgroundRoundRectangle.setColorBackground(0xC080C0FF);
      JHelpButtonBehavior.giveButtonBehavior(key, backgroundRoundRectangle, this.eventManager);

      this.addComponent2D(backgroundRoundRectangle, constraints);
   }

   /**
    * Called when button pressed
    * 
    * @param key
    *           Button ID
    */
   void keyPress(final int key)
   {
      String text;
      List<String> list;
      int length, index;

      switch(key)
      {
         case ACTION_ADD_WORD:
            text = this.editText.getText().trim();

            if(text.length() > 0)
            {
               try
               {
                  this.dictionary.addWord(text, 10);
                  this.textTyped.setText(text);
                  this.editText.setText("");
               }
               catch(final DatabaseException exception)
               {
                  Debug.printException(exception);
               }
            }
         break;
         case ACTION_CLEAR:
            this.arrayInt.clear();
            this.textTyped.setText("");

            for(int i = 0; i < FrameT9.NUMBER_OF_SUGGESTIONS; i++)
            {
               this.suggestions[i].setText("");
            }
         break;
         default:
            if(key < 100)
            {
               // Update suggestions
               for(int i = 0; i < FrameT9.NUMBER_OF_SUGGESTIONS; i++)
               {
                  this.suggestions[i].setText("");
               }

               this.arrayInt.add(key);
               list = this.dictionary.computeSuggestion(FrameT9.NUMBER_OF_SUGGESTIONS, this.arrayInt.toArray());
               length = Math.min(FrameT9.NUMBER_OF_SUGGESTIONS, list.size());

               if(length > 0)
               {
                  text = list.get(0);
                  this.textTyped.setText(text.substring(0, Math.min(this.arrayInt.getSize(), text.length())));
               }
               else
               {
                  this.textTyped.setText("");
               }

               for(int i = 0; i < length; i++)
               {
                  this.suggestions[i].setText(list.get(i));
               }
            }
            else
            {
               // Select a suggestion
               index = key - 100;

               if(index < this.suggestions.length)
               {
                  text = this.suggestions[index].getText();

                  if(text.length() > 0)
                  {
                     try
                     {
                        this.dictionary.addWord(text, 1);
                        this.textTyped.setText(text);
                     }
                     catch(final DatabaseException exception)
                     {
                        Debug.printException(exception);
                     }
                  }
               }
            }

         break;
      }
   }

   /**
    * Called before frame exit <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @return {@code true} if it is allowed to close
    * @see jhelp.gui.JHelpFrame#canCloseNow()
    */
   @Override
   protected boolean canCloseNow()
   {
      try
      {
         this.dictionary.close();
      }
      catch(final DictionaryException exception)
      {
         Debug.printException(exception);
      }

      return true;
   }
}