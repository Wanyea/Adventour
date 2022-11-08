import {useEffect, useState} from 'react'

import iphone from '../../../../../image_assets/eric_assets/iphone-signup.png'
import android from '../../../../../image_assets/eric_assets/android-login.png'
import androidbadge from '../../../../../image_assets/eric_assets/google-play-badge.png'
import iosbadge from '../../../../../image_assets/eric_assets/Download-on-the-App-Store/Download-on-the-App-Store/US/Download_on_App_Store/Black_lockup/SVG/badge.svg'

import './Download.css'

export default function Download(props) {

  const [windowSize, setWindowSize] = useState(window.innerWidth);

  const [showPhone, setShowPhone] = useState(() => {
    if (windowSize <= 1440)
      return false;
    else
      return true;
  })
  

  useEffect(() => {
    getColSize();
    window.addEventListener('resize', () => {
      setWindowSize(window.innerWidth);
      if (windowSize <= 768)
        setShowPhone(false);
      else
        setShowPhone(true);
    })
    
    return () => {
      window.removeEventListener('resize', () => {
        setWindowSize(window.innerWidth);
      });
    }
  }, [windowSize]) 

  const resetAll = () => {
    props.setHome(false);
    props.setFeatures(false);
    props.setDownload(false);
    props.setMeet(false);
    props.setTerms(false);
    props.setPrivacy(false);
  }
  resetAll();
  props.setDownload(true);

  const getColSize = () => {
    if (windowSize <= 1440)
    {
      return 'col-12'
    }
    else
    {
      return 'col-6'
    }
  }


  return (
    <div className='container down'>
      <div className="row">
        <div className="col left">
          <h1>Download the Adventour app on Android and iOS</h1>
          <p>Will be available on the Apple App Store and Google Play Store in November 2022</p>
        </div>
        <div className="col">
          <div className="row ">
            <div className="col center">
              <img className='iphone ' src={iphone} alt="" />
              <img src={iosbadge} alt="" className="ios-badge" />
            </div>
            <div className="col center">
              <img className='android' src={android} alt="" />
              <img src={androidbadge} alt="" className="android-badge" />
            </div>
          </div> 
        </div>
      </div>
    </div>
  )
}
