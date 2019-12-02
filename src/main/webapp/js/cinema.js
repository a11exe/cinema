var seats;
var selectedSeatIndex = "";
var bookingTimeOutSec;

$(document).ready(function () {
  loadProperties();
  loadHall();
});

setInterval(function() {
  loadHall();
} ,10000);

function loadProperties() {
  $.ajax({
    url: "/prop",
    type: 'GET',
    cache: false,
    success: function (data) {
      bookingTimeOutSec = data.timeout;
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log("Error... " + textStatus + "        " + errorThrown);
    },
  });
}

function loadHall() {
  $.ajax({
    url: "/seats",
    type: 'GET',
    cache: false,
    success: function (data) {
      var result = "";
      seats = data.seats;
      var row = "";
      for (var i = 0; i != seats.length; ++i) {
        if (i == 0) {
          row = seats[i].row;
          result += "<div class=\"row justify-content-md-center\">";
              + "        <th>" + row + "</th>"
        } else if (row != seats[i].row) {
          row = seats[i].row;
          result += "</div>\n"
              + "      <div class=\"row justify-content-md-center\">\n";
        }
        result += getCellSeat(i);
      }
      result += "</div>"
      var hall = document.getElementById("hall");
      hall.innerHTML = result;
      markChecked();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log("Error... " + textStatus + "        " + errorThrown);
    },
  });
}

function getCellSeat(i) {
  var seatNum = seats[i].number;
  var row = seats[i].row;
  var seatStr = "<div class=\"col col-sm-2 \" id =\"seat" + i + "\" class=\"alert alert-secondary\"><input id =\"input" + i + "\" type=\"radio\" value=\"" + seats[i].id + "\" disabled> Ряд "
      + row + ", Место " + seatNum + "</div>";
  if (seats[i].state == "BOOKED") {
    seatStr = "<div class=\"col col-sm-2 \" id =\"seat" + i + "\" class=\"alert alert-danger\"><input id =\"input" + i + "\" type=\"radio\" value=\"" + seats[i].id + "\" disabled> Ряд "
        + row + ", Место " + seatNum + "</div>";
  }
  if (seats[i].state == "PENDING") {
    selectedSeatIndex = i;
    seatStr = "<div class=\"col col-sm-2 \" id =\"seat" + i + "\" class=\"alert alert-warning\"><input id =\"input" + i + "\" type=\"radio\" value=\"" + seats[i].id + "\" disabled> Ряд "
        + row + ", Место " + seatNum + "</div>";
  }
  if (seats[i].state == "FREE") {
    seatStr = "<div class=\"col col-sm-2 \" id =\"seat" + i + "\" class=\"alert alert-light\"><input id =\"input" + i + "\" type=\"radio\" name=\"place\" value=\"" + seats[i].id + "\" onclick='startBooking(" + i + ")'> Ряд "
        + row + ", Место " + seatNum + "</div>";
  }
  return seatStr;
}

function markChecked() {
  if (selectedSeatIndex !== "") {
    selectedSeat = seats[selectedSeatIndex];
    if (selectedSeat.state = "PENDING") {
      var selectedSeatEl = document.getElementById("selected");
      var cell = document.getElementById("seat" + selectedSeatIndex);
      $(cell).removeClass("alert-light").removeClass("alert-warning").addClass("alert-success");
      var input = document.getElementById("input" + selectedSeatIndex);
      $(input).prop("checked", true);
      selectedSeatEl.innerHTML = "Selected. Row: " + selectedSeat.row + " seat: " + selectedSeat.number;
    }
  }
}

function startBooking(id) {
  $(document.getElementById("selected")).removeClass("alert-light").addClass("alert-primary");
  if (selectedSeatIndex !== "") {
    var cellPrev = document.getElementById("seat" + selectedSeatIndex);
    $(cellPrev).removeClass("alert-success").addClass("alert-light");
  }
  selectedSeatIndex = id;
  timeOutSec = bookingTimeOutSec;
  countdownTimer = setInterval('bookingTimer()', 1000);
  var seat = seats[selectedSeatIndex];
  $.ajax({
    url: "/book",
    type: 'POST',
    cache: false,
    datatype: 'json',
    contentType: 'application/json;charset=UTF-8',
    data: JSON.stringify(seats[selectedSeatIndex]),
    success: function (data) {
      markChecked();
      loadHall();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      selectedSeatIndex = "";
      console.log("Error... " + textStatus + "        " + errorThrown);
      loadHall();
    },
  })
}

function addBookingInfo() {
  var selectedSeat = document.getElementById("selectedSeat");

  selectedSeat.innerHTML =
      "Вы выбрали ряд " + seats[selectedSeatIndex].row +
      " место " + seats[selectedSeatIndex].number +
      ", Сумма : " + seats[selectedSeatIndex].price + " рублей.";
}

function confirmBooking() {
  var seat = seats[selectedSeatIndex];
  var account = new Object();
  account.name = $('#username').val();
  account.phone = $('#phone').val();
  seat.account = account;

  $.ajax({
    url: "/confirm",
    type: 'POST',
    cache: false,
    datatype: 'json',
    contentType: 'application/json;charset=UTF-8',
    data: JSON.stringify(seat),
    success: function (data) {
      showConfirmationCode("Your booking code<br><b>" + data.code + "</b>");
    },
    error: function (jqXHR, textStatus, errorThrown) {
      selectedSeatIndex = "";
      console.log("Error... " + textStatus + "        " + errorThrown);
      showConfirmationCode("<b>booking failed</b>");
    },
  })
  loadHall();
}

function showConfirmationCode(data) {
  var confirmedResult = document.getElementById("confirmedResut");
  confirmedResult.innerHTML = data;
  $('#confirmed').modal('show');
  loadHall();
}

function bookingTimer() {
  var minutes = Math.round((timeOutSec - 30)/60);
  var remainingSeconds = timeOutSec % 60;
  if (remainingSeconds < 10) {
    remainingSeconds = "0" + remainingSeconds;
  }
  document.getElementById('bookTime').innerHTML = "Booking will be canceled after <span id=\"countdown\" class=\"timer\"></span>";
  document.getElementById('countdown').innerHTML = minutes + ":" + remainingSeconds;
  if (timeOutSec == 0) {
    clearInterval(countdownTimer);
    document.getElementById('countdown').innerHTML = "Booking canceled";
  } else {
    timeOutSec--;
  }

}