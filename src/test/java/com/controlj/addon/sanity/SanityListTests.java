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

import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

public class SanityListTests extends TestCase
{
   
   public void testParseLine()
   {
      SanityCheckConfig sl = new SanityCheckConfig();
      SanityCheckConfigEntry entry;
      entry = sl.parseLine("fred\t0\t100");
      assertEquals("basic parsing w/ ints failed", new SanityCheckConfigEntry("fred",0f,100f), entry);
      assertNull("Bad number format didn't fail", sl.parseLine("george\tasdf\t3d.2"));
      assertNull("Bad number of args didn't fail",sl.parseLine("test\t42.3"));
      assertNull("Empty string didn't fail",sl.parseLine(""));
      assertEquals("basic parsing w/ floats failed", new SanityCheckConfigEntry("fred",42.01f,101.7f), sl.parseLine("fred\t42.01\t101.7"));
   }


   public void testWriteList()
   {
      SanityCheckConfig sl = new SanityCheckConfig();
      sl.add(new SanityCheckConfigEntry("test1", 42, 99));
      sl.add(new SanityCheckConfigEntry("test2", 0.01f, 7.0f));
      StringWriter sw = new StringWriter();
      sl.save(sw);
      String generated[] = sw.toString().split("\r*\n");
      String target[] = {"test1\t42.0\t99.0", "test2\t0.01\t7.0"};

      assertTrue("Simple list output failed", Arrays.equals(generated, target));
   }

   public void testLoad() throws IOException
   {
      SanityCheckConfig sl1 = new SanityCheckConfig();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("test1\t42.0\t99.0");
      pw.println("test2\t0.01\t7.0");
      sl1.load(new StringReader(sw.toString()));
      
      SanityCheckConfig sl2 = new SanityCheckConfig();
      sl2.add(new SanityCheckConfigEntry("test1", 42.0f, 99.0f));
      sl2.add(new SanityCheckConfigEntry("test2", 0.01f, 7.0f));

      assertTrue("Load failed", sl2.getList().equals(sl1.getList()));
   }
}
