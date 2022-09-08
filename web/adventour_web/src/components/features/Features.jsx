import { useState, useEffect } from 'react'
import './Features.css'
import balloon from '../../../../../image_assets/Logo_Balloon_Transparent.png'
import compass from '../../../../../image_assets/eric_assets/compass.png'
import camera from '../../../../../image_assets/eric_assets/camera.png'
import beacon from '../../../../../image_assets/eric_assets/beacon.png'
import flare from '../../../../../image_assets/eric_assets/flare.png'
import heart from '../../../../../image_assets/eric_assets/heart.png'
import { smallScreen } from '../../Constants'

const Features = () => {
  
  const [windowSize, setWindowSize] = useState(window.innerWidth);

  useEffect(() => {
    window.addEventListener('resize', () => {
      setWindowSize(window.innerWidth);
    })
  
    return () => {
      window.removeEventListener('resize', () => {
        setWindowSize(window.innerWidth);
      });
    }
  }, [windowSize])


  return (
    <div className='features'>
      {(windowSize <= smallScreen) ? <h1 className='mobile-header'>Features</h1> : null}
      <div className="row">
        <div className="col center">
          <img className='balloon' src={balloon} alt="" />
          <p>Stop by stop adventure</p>
        </div>
        <div className="col center">
          <img className='compass' src={compass} alt="" />
          <p>Open each destination in your default maps app</p>
        </div>
        <div className="col center">
          <img src={camera} alt="" className="camera" />
          <p>Add photos from each location</p>
        </div>
      </div>
      <div className="row">
        <div className="col center">
          <img src={beacon} alt="" className="beacon" />
          <p>Post Beacons to the Beacon Board</p>
        </div>
        <div className="col center">
          <img src={flare} alt="" className="flare" />
          <p>Give your Beacons flare with title and descriptions with photos for each location</p>
        </div>
        <div className="col center">
          <img src={heart} alt="" className="heart" />
          <p>Use your powers of good to like others' posts so they can rise to the top of Most Liked</p>
        </div>
      </div>
    </div>
  )

}

export default Features