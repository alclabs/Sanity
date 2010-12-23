<%@ page import="java.util.Date" %>
<%@ page import="com.controlj.addon.sanity.SanityCheckConfig" %>
<%@ page import="com.controlj.green.addonsupport.access.SystemConnection" %>
<%@ page import="com.controlj.green.addonsupport.access.ReadAction" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2010 Automated Logic Corporation
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  ~ THE SOFTWARE.
  --%>

<html>
  <head><title>Sanity Check Settings</title></head>
  <style type="text/css">
      body {font-family:sans-serif; color:black; }
      a { color:black; }
      td { padding-left:1em; text-align:left; }
      th { padding-left:1em; text-align:left; }
      input { margin-right:1em; }
      .del { color:#A00000; text-align:center; }
      .title { font-weight:bold; font-size:150%; }
      .purpose { font-style:italic; padding-left:2em; }
      .boxheading { position:relative; left:0px; top:-.7em; background-color:white; padding-left:0.2em; padding-right:0.2em; }
  </style>
<body>
    <div>
        <span class="title">Sanity Check</span>
        <span class="purpose">Check for settings outside of a sane range.</span>
    </div>
    <br/>
    <table cellpadding="0" cellspacing="0">
        <tr>
            <td style="border:solid black 1px; margin-right:100px; padding-right:25px; position:relative;" rowspan="2">
                <span class="boxheading">Present Values to check:</span>
                <c:if test="${not empty sanity_list}">
                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <th>Delete</th>
                        <th style="width:10em">Name</th>
                        <th style="width:5em">Min</th>
                        <th style="width:5em">Max</th>
                    </tr>
                    <c:forEach var="entry" varStatus="loop" items="${sanity_list}">
                        <tr>
                            <td class="del"><a href="controller?action=delete&item=${loop.index}">X</a></td>
                            <td>${entry.displayName}</td>
                            <td>${entry.min}</td>
                            <td>${entry.max}</td>
                        </tr>
                    </c:forEach>
                </table>
                </c:if>
                <form action="controller" method="post">
                <div style="white-space:nowrap; margin-top:20px;">
                    Name: <input type="text" name="rname" size="25"/>
                    Min: <input type="text" name="min" size="4"/>
                    Max: <input type="text" name="max" size="4"/>
                    <input type="submit" name="action" value="Add"/></div>
            </form>
            </td>
            <td rowspan="2" width="50px">&nbsp;</td>
            <td style="border:solid black 1px; height:100px; white-space:nowrap; position:relative;">
                <span class="boxheading">Location:</span>
                <form action="controller">
                    <input type="text" name="location" value="" size="30"/>
                    <input type="submit" name="action" value="Run"/>
                </form>
            </td>
            <%--
            <td style="width:100%; vertical-align:top; text-align:right;">
                <a href="${pageContext.request.contextPath}/controller?action=run"><input type="button" value="Run Report"/></a>
            </td>
             --%>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
    </table>
</body>
</html>