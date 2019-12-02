<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.job4j.model.Seat" %>
<%@ page import="ru.job4j.model.State" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

  <title>Movie tickets online booking</title>
</head>
<body>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
      integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.4.1.js"
        integrity="sha256-WpOohJOqMqqyKL9FccASB9O0KwACQJpFTUBLTYOVvVU="
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
<%--<style>--%>
  <%--.seat-background {--%>
  <%--background: #f99;--%>
  <%--}--%>
<%--</style>--%>

<div class="container">
  <div class="row pt-3">
    <div>
    <h4>Tickets online booking</h4>
    </div>
    <br>


  </div>
  <div class="row float-right">
    <h2>Booking</h2>
    <table class="table table-striped" id="datatable">
      <thead>
      <tr>
        <th>id</th>
        <th>row</th>
        <th>seat</th>
        <th>price</th>
        <th>state</th>
        <th>name</th>
        <th>phone</th>
        <th>code</th>
        <th>cancel</th>
      </tr>
      </thead>
      <tbody id="tbody">
      <c:forEach var="seat" items="${seats}">
        <tr>
          <td><c:out value="${seat.id}"/>
          </td>
          <td><c:out value="${seat.row}"/>
          </td>
          <td><c:out value="${seat.number}"/>
          </td>
          <td><c:out value="${seat.price}"/>
          </td>
          <td><c:out value="${seat.state}"/>
          </td>
          <td><c:out value="${seat.account.name}"/>
          </td>
          <td><c:out value="${seat.account.phone}"/>
          </td>
          <td><c:out value="${seat.code}"/>
          </td>
          <c:choose>
            <c:when test="${seat.state.equals(State.BOOKED)}">
              <td>
                <button type="button"
                        class="btn btn-danger"
                        onclick="">cancel book
                </button>
              </td>
            </c:when>
            <c:otherwise>
              <td></td>
            </c:otherwise>
          </c:choose>

        </tr>
      </c:forEach>
      </tbody>
  </div>
</div>

</body>

</html>