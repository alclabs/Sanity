package com.controlj.addon.sanity;

public class SanityResult
{
   SanityCheckConfigEntry configEntry;
   String displayPath;
   float value;

   public SanityResult(SanityCheckConfigEntry configEntry, String displayPath, float value)
   {
      this.configEntry = configEntry;
      this.displayPath = displayPath;
      this.value = value;
   }

   public SanityCheckConfigEntry getConfigEntry()
   {
      return configEntry;
   }

   public String getDisplayPath()
   {
      return displayPath;
   }

   public float getValue()
   {
      return value;
   }

}
