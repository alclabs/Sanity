/*
 * Copyright (c) 2010 Automated Logic Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.controlj.addon.sanity;

import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.InvalidConnectionRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.io.*;

public class SanityCheckConfig
{
   private static final String SL_SESSION_KEY = "SanityList";
   private List<SanityCheckConfigEntry> list = new ArrayList<SanityCheckConfigEntry>();
   private SystemConnection system;

   private SanityCheckConfig(SystemConnection system)
   {
      this.system = system;
   }

   SanityCheckConfig() { } // default constructor for use in unit test


   void load(Reader in) throws IOException
   {
      BufferedReader reader = new BufferedReader(in);
      String line = null;
      synchronized (list)
      {
         while ((line = reader.readLine()) != null)
         {
            SanityCheckConfigEntry entry = parseLine(line);
            if (entry != null)
            {
               list.add(entry);
            }
         }         
      }
   }

   public void load() throws IOException
   {
      try
      {
         system.runReadAction(new ReadAction()
         {
            public void execute(SystemAccess systemAccess) throws Exception
            {
               DataStore store = systemAccess.getSystemDataStore(SL_SESSION_KEY);
               load(store.getReader());
            }
         });
      }
      catch (ActionExecutionException e)
      {
         throw new IOException("Error reading from data store");
      }
      catch (SystemException e)
      {
         throw new IOException("Error reading from data store");
      }
   }

   public static SanityCheckConfig getListFromRequest(HttpServletRequest req) throws InvalidConnectionRequestException
   {
      SanityCheckConfig list = null;
      HttpSession sess =  req.getSession();
      if (sess != null)
      {
         try
         {
            list = (SanityCheckConfig)sess.getAttribute(SL_SESSION_KEY);
         }
         catch (ClassCastException e) {  } // ignore
      }
      if (list == null)
      {
         list = new SanityCheckConfig(DirectAccess.getDirectAccess().getUserSystemConnection(req));
         try
         {
            list.load();
         }
         catch (IOException e) { } // continue with empty list
      }
      return list;
   }


   public List<SanityCheckConfigEntry> getList()
   {
      return Collections.unmodifiableList(list); 
   }

   public void add(SanityCheckConfigEntry entry)
   {
      synchronized(list)
      {
         list.add(entry);
      }
   }

   void save(Writer writer)
   {
      PrintWriter pw = new PrintWriter(writer);
      synchronized (list)
      {
         for (SanityCheckConfigEntry entry : list)
         {
            pw.println(entry.asTDString());
         }
      }
      pw.flush();
      pw.close();
   }

   public void delete(int index)
   {
      synchronized (list)
      {
         list.remove(index);
      }
   }

   public void save() throws IOException
   {
      try
      {
         system.runWriteAction("Writing defaults to system datastore", new WriteAction()
         {
            public void execute(WritableSystemAccess systemAccess) throws Exception
            {
               DataStore store = systemAccess.getSystemDataStore(SL_SESSION_KEY);
               save(store.getWriter());
            }
         });
      }
      catch (Exception e)
      {
         throw new IOException("Error writing to data store");
      }
   }

   public Iterator getIterator()
   {
      return list.iterator();
   }

   /**<!===== parseLine ======================================================>
      Parse a tab separated line in the form refname\tmin\tmax into a
     {@link SanityCheckConfigEntry}.  Returns null if the line does not parse correctly.
      <!      Name       Description>
      @param  line       .
      @return SanityEntry or null on error.
      @author sappling
   <!=======================================================================>*/
   SanityCheckConfigEntry parseLine(String line)
   {
      String[] parts = line.split("\t");
      if (parts.length == 3)
      {
         String name = parts[0];
         try
         {
            float min = Float.parseFloat(parts[1]);
            float max = Float.parseFloat(parts[2]);
            return new SanityCheckConfigEntry(name, min, max);
         }
         catch (NumberFormatException e) { } // fall through and return null
      }
      return null;
   }
}
