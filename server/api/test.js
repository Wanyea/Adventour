const axios = require('axios')
const router = require('express').Router()
require("dotenv").config(); 

module.exports = router
const key = process.env.GOOGLE_MAPS_API_KEY
router.get('/nearby-search', async (req, res, next) => {
 try {
    const keyword = "bowling zoo fun local"
    const location = "28.605746775904176%2C-81.3645762367732"
    const radius = "8000"
    const {data} = await axios.get(
   
`https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=${key}&location=${location}&radius=${radius}&keyword=${keyword}`
   )
   res.json(data)
   console.log(data);
   } 
 catch (err) {
  next(err)
}
})