package com.controlj.addon.sanity;

import com.controlj.green.addonsupport.InvalidConnectionRequestException;
import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.access.aspect.PhysicalPoint;
import com.controlj.green.addonsupport.access.aspect.PresentValue;
import com.controlj.green.addonsupport.access.value.FloatValue;
import com.controlj.green.addonsupport.access.value.InvalidValueException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**<!=========================================================================>
   <p>
   Controller servlet.  Determines what to do based on &quot;action&quot;
   parameter, performs the action and forwards to the correct view.  All the
   view jsp files are hidden under WEB-INF so they can't be accessed without
   this controller.
   </p>
   <p>
   Many of the action methods add their results to the request so they can
   be accessed in the page they forward to.
   </p>
    
   @author sappling
<!==========================================================================>*/
public class Controller extends HttpServlet
{
   public static final String ACTION_PARAM_NAME = "action";
   public static final String ACTION_ADD = "add";
   public static final String ACTION_DELETE = "delete";
   public static final String ACTION_VIEW = "view";
   public static final String ACTION_RUN = "run";

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      doPost(request, response);
   }

   /**<!===== doPost =========================================================>
    Forward request to the right action method.  By default, it just
    displays the config page.
    <!      Name             Description>
    @param  request          .
    @param  response         .
    @throws javax.servlet.ServletException .
    @throws java.io.IOException      .
    @author sappling
    <!=======================================================================>*/
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String action = request.getParameter(ACTION_PARAM_NAME);
      if (action == null)
      {
         action = "unknown";
      }
      if (action.equalsIgnoreCase(ACTION_ADD))
      {
         doAddEntry(request, response);
      }
      else if (action.equalsIgnoreCase(ACTION_DELETE))
      {
         doDeleteEntry(request, response);
      }
      else if (action.equalsIgnoreCase(ACTION_RUN))
      {
         doRun(request, response);
      }
      else
      {
         doViewConfig(request, response);
      }
   }

   /**<!=========================================================================>
    AspectAcceptor that accepts PresentValues that have a Display Name that is
    used as a key in the specified map.  The PresentValue must also be Analog,
    and if it is a PhysicalPoint, it must be enabled.
    @author sappling
    <!==========================================================================>*/
   private class NamedFloatValueAcceptor implements AspectAcceptor<PresentValue>
   {
      private Map<String, SanityCheckConfigEntry> map;

      private NamedFloatValueAcceptor(Map<String, SanityCheckConfigEntry> map)
      {
         this.map = map;
      }

      public boolean accept(PresentValue pv)
      {
         boolean result = false;

         try
         {
            Location location = pv.getLocation();
            if (map.containsKey(location.getDisplayName()))
            {
               // If it is a physical point
               if (location.hasAspect(PhysicalPoint.class))
               {
                  // Make sure it is enabled
                  result = location.getAspect(PhysicalPoint.class).isEnabled() && pv.getValue() instanceof FloatValue;
               }

               // Make sure it is an analog value
               result &= pv.getValue() instanceof FloatValue;
            }
         }
         catch (NoSuchAspectException e)
         {
            e.printStackTrace();
         }

         return result;
      }
   }

   /**<!===== doRun ==========================================================>
    Runs the sanity check report, putting the results into a List of
    SanityResult objects, then forwarding the HttpServletRequest to the run.jsp
    page for it to display the results.
    <!      Name             Description>
    @param  request          Incoming HttpServletRequest.
    @param  response         HttpServletResponse used to respond to the browser.
    @throws javax.servlet.ServletException .
    @throws java.io.IOException      .
    @author sappling
    <!=======================================================================>*/
   private void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      final List<SanityResult> results = new ArrayList<SanityResult>();
      final String location = request.getParameter("location");

      try
      {
         // Make a Map of Display Name Strings -> SanityCheckConfigEntries
         final SanityCheckConfig sl = SanityCheckConfig.getListFromRequest(request);
         final Map<String, SanityCheckConfigEntry> map = new HashMap<String, SanityCheckConfigEntry>();

         for (SanityCheckConfigEntry entry : sl.getList())
         {
            map.put(entry.getDisplayName(), entry);
         }

         SystemConnection connection = DirectAccess.getDirectAccess().getUserSystemConnection(request);
         connection.runReadAction( FieldAccessFactory.newFieldAccess(), new ReadAction()
         {
            public void execute(SystemAccess access) throws Exception
            {
               // Get the starting location as a descendant of the root of the geographic tree
               Location start = access.getTree(SystemTree.Geographic).getRoot().getDescendant(location);

               // Find the appropriate Analog Present Values
               Collection<PresentValue> pvs = start.find(PresentValue.class, new NamedFloatValueAcceptor(map));
               for (PresentValue pv : pvs)
               {
                  FloatValue valueObj = (FloatValue) pv.getValue();
                  SanityCheckConfigEntry entry = map.get(pv.getLocation().getDisplayName());
                  try
                  {
                     float value = valueObj.getValue();
                     if (value > entry.getMax() || value < entry.getMin())
                     {
                        results.add(new SanityResult(entry,
                              pv.getLocation().getDisplayPath(),
                              value));
                     }
                  }
                  catch (InvalidValueException e)
                  {
                     throw new ServletException("Error reading value", e);
                  }
               }
            }
         });
      }
      catch (InvalidConnectionRequestException e)
      {
         throw new ServletException("Bad connection", e);
      }
      catch (SystemException e)
      {
         throw new ServletException( e);
      }
      catch (ActionExecutionException e)
      {
         throw new ServletException(e);
      }
      request.setAttribute("results", results);
      request.getRequestDispatcher("/WEB-INF/run.jsp").forward(request, response);
   }

   /**<!===== doViewConfig ===================================================>
    Gets the SanityCheckConfig object (containing the list of SanityCheckConfigEntrys)
    and stores it in the request as a sanity_list attribute.
    <!      Name             Description>
    @param  request          .
    @param  response         .
    @throws javax.servlet.ServletException .
    @throws java.io.IOException      .
    @author sappling
    <!=======================================================================>*/
   private void doViewConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      try
      {
         request.setAttribute("sanity_list", SanityCheckConfig.getListFromRequest(request).getList());
      }
      catch (InvalidConnectionRequestException e)
      {
         throw new ServletException("Bad connection", e);
      }
      request.getRequestDispatcher("/WEB-INF/config.jsp").forward(request, response);
   }

   /**<!===== doAddEntry =====================================================>
    Adds a new entry to the SanityCheckConfig.
    <!      Name             Description>
    @param  request          .
    @param  response         .
    @throws javax.servlet.ServletException .
    @throws java.io.IOException      .
    @author sappling
    <!=======================================================================>*/
   private void doAddEntry(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      SanityCheckConfig sl;
      try
      {
         sl = SanityCheckConfig.getListFromRequest(request);
      }
      catch (InvalidConnectionRequestException e)
      {
         throw new ServletException("Invalid connection", e);
      }

      String name = request.getParameter("rname");
      String minString = request.getParameter("min");
      String maxString = request.getParameter("max");
      try
      {
         float min = Float.parseFloat(minString);
         float max = Float.parseFloat(maxString);
         sl.add(new SanityCheckConfigEntry(name, min, max));
         sl.save();
      }
      catch (NumberFormatException e)
      {
         log("Error adding SanityEntry, min or max not a float", e);
      }
      catch (IOException ioe)
      {
         throw new ServletException("Error saving SanityList to disk", ioe);
      }
      forwardToViewConfig(request, response);
   }

   /**<!===== doDeleteEntry ==================================================>
    Removes an entry from the SanityCheckConfig.
    <!      Name             Description>
    @param  request          .
    @param  response         .
    @throws javax.servlet.ServletException .
    @throws java.io.IOException      .
    @author sappling
    <!=======================================================================>*/
   private void doDeleteEntry(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      SanityCheckConfig sl;
      try
      {
         sl = SanityCheckConfig.getListFromRequest(request);
      }
      catch (InvalidConnectionRequestException e)
      {
         throw new ServletException("Inmvalid Connection", e);
      }

      String itemString = request.getParameter("item");
      try
      {
         int index = Integer.parseInt(itemString);
         sl.delete(index);
         sl.save();
      }
      catch (NumberFormatException e)
      {
         log("Error deleting SanityEntry, item not an int", e);
      }
      catch (IOException ioe)
      {
         throw new ServletException("Error saving SanityList to disk", ioe);
      }
      forwardToViewConfig(request, response);
   }

   private void forwardToViewConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      response.sendRedirect(response.encodeRedirectURL("controller"));
   }
}
