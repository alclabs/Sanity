<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Sanity Check</title></head>
  <body>
  <table>
      <c:forEach var="result" items="${results}">
          <tr>
              <td>${result.displayPath}</td>
              <td>${result.value}</td>
          </tr>
      </c:forEach>
  </table>
  <br /><br/>
  <a href="controller">Back to Configuration</a>
  </body>
</html>