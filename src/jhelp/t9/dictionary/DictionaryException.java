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

import jhelp.util.text.UtilText;

/**
 * Exception in dictionary
 * 
 * @author JHelp
 */
public class DictionaryException
      extends Exception
{
   /**
    * Create a new instance of DictionaryException
    * 
    * @param message
    *           Message
    */
   public DictionaryException(final Object... message)
   {
      super(UtilText.concatenate(message));
   }

   /**
    * Create a new instance of DictionaryException
    * 
    * @param cause
    *           Cause of exception
    * @param message
    *           Message
    */
   public DictionaryException(final Throwable cause, final Object... message)
   {
      super(UtilText.concatenate(message), cause);
   }
}