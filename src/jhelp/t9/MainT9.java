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
package jhelp.t9;

import jhelp.t9.dictionary.Dictionary;
import jhelp.t9.ui.FrameT9;
import jhelp.util.debug.Debug;
import jhelp.util.gui.UtilGUI;

/**
 * Dictionary usage example
 * 
 * @author JHelp
 */
public class MainT9
{
   /**
    * Launch the sample
    * 
    * @param args
    *           Unused
    */
   public static void main(final String[] args)
   {
      UtilGUI.initializeGUI();

      try
      {
         final Dictionary dictionary = new Dictionary("fr");
         final FrameT9 frameT9 = new FrameT9(dictionary);
         frameT9.setVisible(true);
         frameT9.setAutomaticRefresh(true);
      }
      catch(final Exception exception)
      {
         Debug.printException(exception, "Failed to create dictionary for fr");
      }
   }
}