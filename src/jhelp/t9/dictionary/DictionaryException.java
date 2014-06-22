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