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

public class SanityCheckConfigEntry
{
   private String displayName;
   private float min;
   private float max;

   public SanityCheckConfigEntry(String displayName, float min, float max)
   {
      this.displayName = displayName;
      this.min = min;
      this.max = max;
   }


   public String getDisplayName() { return displayName; }

   public float getMin() { return min; }

   public float getMax() { return max; }

   public String asTDString() { return displayName +"\t"+min+"\t"+max; };

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof SanityCheckConfigEntry)
      {
         SanityCheckConfigEntry other = (SanityCheckConfigEntry)obj;
         return ( getDisplayName().equals(other.getDisplayName()) &&
              getMin() == other.getMin() &&
              getMax() == other.getMax());
      }
      return false;
   }
}
