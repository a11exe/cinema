var seats;
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

function showRows() {
  var result = "";
  var row = "<div>";
  for (var i = 0; i !== seats.length; ++i) {
    if (row !== seats[i].row) {
      row = seats[i].row;
      result += "</div>"
          + "      <div class=\"row justify-content-md-center\">"
          + "      <div class=\"seat\"><button  type=\"button\" class=\"btn seat\">" + row + "</button></div>"
    }
  }
  result += "</div>"
  var rows = document.getElementById("rows");
  rows.innerHTML = result;
}

function showSeats() {
  var result = "";
  var row = "";
  for (var i = 0; i !== seats.length; ++i) {
    if (i === 0) {
      row = seats[i].row;
      result += "<div class=\"row justify-content-md-center\">";
    } else if (row !== seats[i].row) {
      row = seats[i].row;
      result += "</div>"
          + "      <div class=\"row justify-content-md-center\">";
    }
    result += getCellSeat(i);
  }
  result += "</div>"
  var hall = document.getElementById("hall");
  hall.innerHTML = result;
}

function loadHall() {
  $.ajax({
    url: "/seats",
    type: 'GET',
    cache: false,
    success: function (data) {
      seats = data.seats;
      showRows();
      showSeats();
      showSelected();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log("Error... " + textStatus + "        " + errorThrown);
    },
  });
}

function getCellSeat(i) {
  var seatNum = seats[i].number;
  var row = seats[i].row;
  var seatStr;
  if (seats[i].state === "BOOKED") {
    seatStr = "<button type=\"button\" class=\"btn booked seat\">" + seatNum + "</button>";
  }
  if (seats[i].state === "PENDING") {
    seatStr = "<button type=\"button\" class=\"btn pending seat\">" + seatNum + "</button>";
  }
  if (seats[i].state === "SELECTED") {
    seatStr = "<button type=\"button\" class=\"btn selected seat\" onclick=cancelBooking(" + i + ")>" + seatNum + "</button>";
  }
  if (seats[i].state === "FREE") {
    seatStr = "<button type=\"button\" class=\"btn free seat\" onclick=startBooking(" + i + ")>" + seatNum + "</button>";
  }
  return seatStr;
}

function showSelected() {
  var result = "Selected: <br>";
  var total = 0;
  for (var i = 0; i !== seats.length; ++i) {
    if (seats[i].state === "SELECTED") {
      row = seats[i].row;
      result += "Row: " + seats[i].row + " seat: " + seats[i].number + " price: $" + seats[i].price + "<br>";
      total += seats[i].price;
    }
  }
  result += "<hr/>";
  result += "<span class=\"text-dark\"> total: $" + total + "</span>";
  result += "<hr>";
  document.getElementById("selected").innerHTML = result;
}

function startBooking(i) {
  var seat = seats[i];
  $.ajax({
    url: "/startbooking",
    type: 'POST',
    cache: false,
    datatype: 'json',
    contentType: 'application/json;charset=UTF-8',
    data: JSON.stringify(seats[i]),
    success: function (data) {
      timeOutSec = bookingTimeOutSec;
      countdownTimer = setInterval('bookingTimer()', 1000);
      loadHall();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log("Error... " + textStatus + "        " + errorThrown);
      loadHall();
    },
  })
}

function cancelBooking(i) {
  var seat = seats[i];
  $.ajax({
    url: "/cancelbooking",
    type: 'POST',
    cache: false,
    datatype: 'json',
    contentType: 'application/json;charset=UTF-8',
    data: JSON.stringify(seats[i]),
    success: function (data) {
      timeOutSec = bookingTimeOutSec;
      countdownTimer = setInterval('bookingTimer()', 1000);
      loadHall();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log("Error... " + textStatus + "        " + errorThrown);
      loadHall();
    },
  })
}

function addBookingInfo() {
  var selectedSeat = document.getElementById("selectedSeat");

  selectedSeat.innerHTML =
      "Please confirm booking";
}

function confirmBooking() {
  var account = new Object();
  account.name = $('#username').val();
  account.phone = $('#phone').val();

  $.ajax({
    url: "/confirm",
    type: 'POST',
    cache: false,
    datatype: 'json',
    contentType: 'application/json;charset=UTF-8',
    data: JSON.stringify(account),
    success: function (data) {
      showConfirmationCode("Your booking code<br><b>" + data + "</b>");
    },
    error: function (jqXHR, textStatus, errorThrown) {
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
  document.getElementById('bookTime').innerHTML = "Booking will be canceled <br> after <span id=\"countdown\" class=\"timer\"></span>";
  document.getElementById('countdown').innerHTML = minutes + ":" + remainingSeconds;
  if (timeOutSec === 0) {
    clearInterval(countdownTimer);
    document.getElementById('countdown').innerHTML = "Booking canceled";
  } else {
    timeOutSec--;
  }

}