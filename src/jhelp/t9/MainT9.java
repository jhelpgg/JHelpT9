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