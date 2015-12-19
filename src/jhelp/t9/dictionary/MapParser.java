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

import java.io.InputStream;
import java.util.Hashtable;

import jhelp.util.debug.Debug;
import jhelp.util.debug.DebugLevel;
import jhelp.xml.ExceptionParseXML;
import jhelp.xml.ExceptionXML;
import jhelp.xml.InvalidParameterValueException;
import jhelp.xml.ParseXMLlistener;
import jhelp.xml.ParserXML;

/**
 * Parse T9 key map
 * 
 * @author JHelp
 */
class MapParser
      implements ParseXMLlistener
{
   /**
    * Key markup with a {@link #PARAMETER_NAME} (Key identifier in [0, 9]) and {@link #PARAMETER_CHARACTERS} (list of characters
    * represents by the key)
    */
   private static final String MARKUP_KEY           = "Key";
   /** Characters considers as separators */
   private static final String MARKUP_SEPARATORS    = "Separators";
   /** T9 main markup */
   private static final String MARKUP_T9            = "T9";
   /** String parameter of {@link #MARKUP_KEY} that contains the list of characters manage by the key */
   private static final String PARAMETER_CHARACTERS = "characters";
   /** Integer parameter of {@link #MARKUP_KEY} that contains the key identifier */
   private static final String PARAMETER_NAME       = "name";
   /** Indicates if parse as enter inside {@link #MARKUP_SEPARATORS} */
   private boolean             inSeparators;
   /** List of keys to fill */
   private final String[]      keys;
   /** List of characters considered as separators */
   private String              separators;

   /**
    * Create a new instance of MapParser
    * 
    * @param mapStream
    *           Stream to parse
    * @param keys
    *           List of keys to fill
    * @throws ExceptionParseXML
    *            On parsing issue
    */
   public MapParser(final InputStream mapStream, final String[] keys)
         throws ExceptionParseXML
   {
      this.keys = keys;
      final ParserXML parserXML = new ParserXML();
      parserXML.parse(this, mapStream);
   }

   /**
    * Called when comment found in XML <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param comment
    *           Comment found
    * @see jhelp.xml.ParseXMLlistener#commentFind(java.lang.String)
    */
   @Override
   public void commentFind(final String comment)
   {
   }

   /**
    * Called when a markup close found in XML <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param markupName
    *           Closed markup
    * @throws ExceptionXML
    *            If close this markup is not allow now
    * @see jhelp.xml.ParseXMLlistener#endMarkup(java.lang.String)
    */
   @Override
   public void endMarkup(final String markupName) throws ExceptionXML
   {
      if(MapParser.MARKUP_SEPARATORS.equals(markupName) == true)
      {
         this.inSeparators = false;
      }
   }

   /**
    * Called when all XML parsed <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @throws ExceptionXML
    *            If XML shouldn't end now
    * @see jhelp.xml.ParseXMLlistener#endParse()
    */
   @Override
   public void endParse() throws ExceptionXML
   {
      if(this.separators == null)
      {
         throw new ExceptionXML("Map not contains separators");
      }

      for(int i = 0; i < 10; i++)
      {
         if(this.keys[i] == null)
         {
            throw new ExceptionXML("Map not contains key " + i);
         }
      }
   }

   /**
    * Called when error happen during XML parsing <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param exceptionParseXML
    *           Error happened
    * @see jhelp.xml.ParseXMLlistener#exceptionForceEndParse(jhelp.xml.ExceptionParseXML)
    */
   @Override
   public void exceptionForceEndParse(final ExceptionParseXML exceptionParseXML)
   {
      Debug.printException(exceptionParseXML, "Issue while parsing ...");
   }

   /**
    * List of characters considered as separators
    * 
    * @return List of characters considered as separators
    */
   public String getSeparators()
   {
      return this.separators;
   }

   /**
    * Called when XML open a markup <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param markupName
    *           Markup name
    * @param parameters
    *           Markup parameters
    * @throws ExceptionXML
    *            If XML not well formed (Missing requiered parameters, wrong parameter type, ....)
    * @see jhelp.xml.ParseXMLlistener#startMakup(java.lang.String, java.util.Hashtable)
    */
   @Override
   public void startMakup(final String markupName, final Hashtable<String, String> parameters) throws ExceptionXML
   {
      if(MapParser.MARKUP_T9.equals(markupName) == true)
      {
         return;
      }

      if(MapParser.MARKUP_KEY.equals(markupName) == true)
      {
         final int name = ParserXML.obtainInteger(markupName, parameters, MapParser.PARAMETER_NAME, true, -1);
         final String characters = ParserXML.obtainParameter(markupName, parameters, MapParser.PARAMETER_CHARACTERS, true);

         if((name < 0) || (name > 9))
         {
            throw new InvalidParameterValueException(MapParser.PARAMETER_NAME, markupName, "Name MUST be an integer in [0, 9], not " + name);
         }

         this.keys[name] = characters;

         return;
      }

      if(MapParser.MARKUP_SEPARATORS.equals(markupName) == true)
      {
         this.inSeparators = true;

         if(this.separators != null)
         {
            throw new ExceptionXML("Meet a second separators definition");
         }

         return;
      }

      Debug.println(DebugLevel.WARNING, "Ignored markup : ", markupName);
   }

   /**
    * Called when XML parsing starts <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @see jhelp.xml.ParseXMLlistener#startParse()
    */
   @Override
   public void startParse()
   {
      this.inSeparators = false;
      this.separators = null;
   }

   /**
    * Called when meet text in XML <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param text
    *           Text meet
    * @throws ExceptionXML
    *            If text whouldn't be their, incorrect, ...
    * @see jhelp.xml.ParseXMLlistener#textFind(java.lang.String)
    */
   @Override
   public void textFind(final String text) throws ExceptionXML
   {
      if(this.inSeparators == true)
      {
         this.separators = text;
      }
   }
}