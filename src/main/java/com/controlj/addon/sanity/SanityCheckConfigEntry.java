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
