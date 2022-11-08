/* Adventour Node Server */
/* Serving up Foursquare requests since September 2022 */
/* Author: Glenn Eric Hartwell */

const PORT = process.env.PORT || 5000

// Dependencies
const express = require("express")
const cors = require("cors")
const bodyParser = require("body-parser")

const admin = require('firebase-admin')
var serviceAccount = require("./firebaseAdmin.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
})


// Create Express application
const app = express()
app.use(cors())
app.use(express.json())
app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json())

// var getPlaceById = require("./endpoints/api.adventour.get-place-by-id")
var getFoursquarePlaces = require("./endpoints/api.adventour.get-foursquare-places")
var getAdventourPlace = require("./endpoints/api.adventour.get-adventour-place")
var sendEmailReport = require("./endpoints/api.adventour.send-email-report")

// app.use(getPlaceById);
app.use(getFoursquarePlaces)
app.use(getAdventourPlace)

app.use(sendEmailReport)

// HTML at root that has a link to Swaggerhub documentation
app.get('/', (req, res) => {
    res.send("This is the root of the Adventour Node/Express Server<br/>Available endpoints can be found <a href=\"https://app.swaggerhub.com/apis/glerichartwell/Adventour/1.0.0#/developers/\">here</a>")
})

app.set('json spaces', 2)
app.listen(PORT);
console.log("listening", PORT)