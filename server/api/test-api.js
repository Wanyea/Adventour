const axios = require('axios')
const router = require('express').Router()

module.exports = router
const key = process.env.GOOGLE_MAPS_API_KEY
router.get('/test-api', async (req, res, next) => {
 try {
    var ret = {
        message: "This is a test.",
        error: ""
    }

    res.status(200).json(ret)
   } 
 catch (err) {
  next(err)
}
})