/* Adventour Node Server */
/* Serving up Google Maps requests since 2022 */
/* Author: Glenn Eric Hartwell */

const PORT = 5000

// Dependencies
const express = require("express")
const cors = require("cors")


// Create Express application
const app = express()
app.use(cors())
app.use(express.json())

var test = require("./api/test")
var testapi = require("./api/test-api")

app.use("/", test)

app.use("/", testapi)

app.get('/', (req, res) => {
    res.send("Hello")
})

app.get("/test", (req, res) => {
    res.status(200).json({
        message: "This is a test",
        error: ""
    })
})


app.listen(PORT);
console.log("listening", PORT)