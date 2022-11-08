import { useState, useEffect } from 'react'
import './Home.css'
import '../../css/Containers.css'
import iPhoneBeacon from '../../../../../image_assets/eric_assets/iphone-beacon.png'
import iPhoneLanding from '../../../../../image_assets/eric_assets/iphone-landing.png'
import iPhoneStart from '../../../../../image_assets/eric_assets/iphone-start.png'
import { smallScreen } from '../../Constants'

export default function Home(props) {

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

  const resetAll = () => {
    props.setHome(false);
    props.setFeatures(false);
    props.setDownload(false);
    props.setMeet(false);
    props.setTerms(false);
    props.setPrivacy(false);
  }
  resetAll();
  props.setHome(true);

  return (
    <div className="container home">
      
      <div className="row">
        <div className="col left">
          <h1 className='left-pad'>A simple way to have an adventure.</h1>
          <p className='left-pad'> 
            Adventour, a portmanteau of the words “adventure” and “tour”, is the brainchild of University of Central Florida student Wanyea Barbel. 
            He crafted this idea at Shell Hacks 2021 and later pitched it as a Senior Design project in the spring of 2022. 
            It was no surprise that this was a popular project choice with six different students making it their first choice project. 
            Only five would be chosen to take on this mobile application, but another would soon join to round out the team.  
          </p>
        </div>
        <div className="col center">
            <img src={iPhoneLanding} alt="" className="phone"/>
        </div>
      </div>

      <div className={((windowSize > smallScreen) ? 'row-reverse' : 'row') + ' middle-pad'}>
        <div className="col left">
          <h1 className='right-pad'>Adventours take you where you want to go.</h1>
          <p className='right-pad'> 
            Our proprietary algorithm helps you find places that fit your interests. 
            Using Foursquare's trusted business data, we make recommendations that suit your mood. Discover great local businesses and build an Adventour you will remember!   
          </p>
        </div>
        <div className="col center">
          <img src={iPhoneStart} className="phone"/>
        </div>
      </div>
      
      <div className="row">
        <div className="col left">
          <h1 className='left-pad'>Send out your Beacon for all to see!</h1>
          <p className='left-pad'> 
            Once your Adventour is complete you can post a Beacon for other Adventourists to see! 
            Beacons are attached to locations all over the United States and give you the opportunity to experience others’ Adventours for yourself.   
          </p>
          {/* Attach photos to your Adventour and share them with people across the country! */}
        </div>
        <div className="col center">
          <img src={iPhoneBeacon} alt="" className="phone"/>
        </div>
      </div>

    </div>
  )
  
}
