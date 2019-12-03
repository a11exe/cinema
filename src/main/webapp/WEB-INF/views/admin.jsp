<html>
<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

  <title>Movie tickets online booking (admin panel)</title>
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
<script>

  var seats;

  $(document).ready(function () {
    loadBooked();
  });

  function nullToEmptyStr(obj) {
    if (obj === null) {
      return "";
    } else {
      return obj;
    }
  }

  function getRowClass(state) {
    if (state === "BOOKED") {
      return "class=\"table-danger\"";
    }
    if (state === "PENDING") {
     return "class=\"table-warning\""
    }
  }

  function loadBooked() {
    $.ajax({
      url: "/bookadmin",
      type: 'GET',
      cache: false,
      success: function (data) {
        var result = "";
        seats = data.seats;
        var row = "";
        debugger
        for (var i = 0; i != seats.length; ++i) {
            result += "<tr " + getRowClass(seats[i].state) + ">"
            + "<td>" + seats[i].id + "</td>"
            + "<td>" + seats[i].row + "</td>"
            + "<td>" + seats[i].number + "</td>"
            + "<td>" + seats[i].price + "</td>"
            + "<td>" + seats[i].state + "</td>"
            + "<td>" + nullToEmptyStr(seats[i].account.name) + "</td>"
            + "<td>" + nullToEmptyStr(seats[i].account.phone) + "</td>"
            + "<td>" + nullToEmptyStr(seats[i].code) + "</td>";

            if (seats[i].state === "BOOKED") {
              result += "<td>" +
                  "<button type=\"button\"\n"
                  + "                        class=\"btn btn-danger\"\n"
                  + "                        onclick=\"cancelBooking(" + i + ")\">cancel"
              + "</td>"
            } else {
              result += "<td></td>"
            }
          result += "</tr>"
        }

        var tbody = document.getElementById("tbody");
        tbody.innerHTML = result;
      },
      error: function (jqXHR, textStatus, errorThrown) {
        console.log("Error... " + textStatus + "        " + errorThrown);
      },
    });
  }

  function cancelBooking(i) {
    $.ajax({
      url: "/bookadmin",
      type: 'POST',
      cache: false,
      datatype: 'json',
      contentType: 'application/json;charset=UTF-8',
      data: JSON.stringify(seats[i]),
      success: function (data) {
        loadBooked();
      },
      error: function (jqXHR, textStatus, errorThrown) {
        console.log("Error... " + textStatus + "        " + errorThrown);
        loadBooked();
      },
    })
  }

</script>


<div class="container">
  <div class="row pt-3">
    <div>
    <h4>Tickets online booking</h4>
    </div>
    <br>


  </div>
  <div class="row float-right">
    <h2>Booking admin panel</h2>
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

      </tbody>
    </table>
  </div>
</div>

</body>

</html>