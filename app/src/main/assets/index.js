//this file is for a separate program that uses PubNub's project EON to display realtime Muni buses on a map. It gets the location from XML NextBus file

var GtfsRealtimeBindings = require('gtfs-realtime-bindings');
var request = require('request');

'use strict';

function test() {
function make2DArray(rows) {
    var arr = [];
    for(var y = 0; y < rows; y++) {
        arr[y] = [];
    }
    return arr;
}
var chan = "pubTrans";
var pub_key = 'pub-c-cfd86269-b02b-4004-97a6-92ca5f582fce';
var sub_key = 'sub-c-e3dc294e-4568-11e6-bfbb-02ee2ddab7fe';
var pubnub_lat = 37.783431; //ty GoogleMaps
var pubnub_long = -122.399248;
var polyline = L.polyline([], {color:'red', fillColor:'red'});
var firstSitePart = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=sf-muni&r=";
var secondSitePart = "&t=1144953500233";
var lat1, long1, lat2, long2, lat3, long3;
var totalStops = new Object();
var latArr = make2DArray(3);
var longArr = make2DArray(3);
var sitesToCheck = [];
var siteToCheck1, siteToCheck2, siteToCheck3;

var pb= PUBNUB.init({
    //ssl: true,
    publish_key: pub_key,
    subscribe_key: sub_key
});

//get data from xml link
$(document).ready(function () {
    for(var i = 0; i < 3; i++) {
        var busNum = i+1;
        if(busNum == 1) {
            siteToCheck1 = firstSitePart + busNum + secondSitePart;
            console.log(siteToCheck1);
            sitesToCheck.push(siteToCheck1);
        }
        else if (busNum == 2) {
            siteToCheck2 = firstSitePart + busNum + secondSitePart;
            console.log(siteToCheck2);
            sitesToCheck.push(siteToCheck2);
        }
        else if (busNum == 3) {
            siteToCheck3 = firstSitePart + busNum + secondSitePart;
            console.log(siteToCheck3);
            sitesToCheck.push(siteToCheck3);
        }


        console.log(sitesToCheck);
         $.each(sitesToCheck, function (index1, value ) {
            $.get(value, function (xml) {
                console.log(value);
                $('vehicle', xml).each(function (index, vehicle) {
                    totalStops[i] = {'lat': $(vehicle).attr('lat'), 'lon': $(vehicle).attr('lon')};
                    if($(vehicle).attr('lat') != null) {
                        latArr[index1][index] = {'lat': $(vehicle).attr('lat')};
                    }
                    else {
                        latArr[index1][index] = {'lat': 37.783539 };
                    }
                    if($(vehicle).attr('lon') != null) {
                        longArr[index1][index] = {'lon': $(vehicle).attr('lon')};
                    }
                    else {
                        longArr[index1][index] = {'lon': -122.399267 };
                    }
                    console.log(index1, latArr[index1][0].lat);
                    console.log(index1, longArr[index1][0].lon);
                }); //each
                setInterval(function() {
                    for(var i = 0; i < latArr.length; i++) {
                        pb.publish({
                            channel:  chan,
                            message: [
                                {"latlng": [latArr[0][i].lat, longArr[0][i].lon ] }, //0 = Muni 1-California
                                {"latlng": [latArr[1][i].lat, longArr[1][i].lon ] }, //1 = Muni 2-
                                {"latlng": [latArr[2][i].lat, longArr[2][i].lon ] } //2 = Muni 3-
                            ]
                        });
                    }
                }, 5000);
            });
         });
    }
    var map = eon.map({
        id: 'map',
        pubnub: pb,
        mb_id: 'mapbox.streets',
        mb_token: 'pk.eyJ1IjoibGl6emllcGlrYSIsImEiOiJjaXFpZWk4d2cwNjd2ZnJtMXhiaTRmbXNpIn0.N2u0leIXpKQSC6Er0YkAjg',
        center: [pubnub_lat, pubnub_long],
        channel: chan,
        rotate: true,
        message: function (data) {
            //for(var i = 0; i < 3; i++) {
            map.setView(data[1].latlng); //13 = zoom, set location
            //}
        }
    });
});
void(0);
//var requestSettings = {
//  method: 'GET',
//  url: 'URL OF YOUR GTFS-REALTIME SOURCE GOES HERE',
//  encoding: null
//};
//request(requestSettings, function (error, response, body) {
//  if (!error && response.statusCode == 200) {
//    var feed = GtfsRealtimeBindings.FeedMessage.decode(body);
//    feed.entity.forEach(function(entity) {
//      if (entity.trip_update) {
//        console.log(entity.trip_update);
//      }
//    });
//  }
//});
}


