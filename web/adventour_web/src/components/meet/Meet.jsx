import { useState, useEffect } from 'react'
import './Meet.css'
import '../../css/Containers.css'
import { Wanyea, Charley, Dominique, Eric, Patrick, NicNac } from '../../Bio'
import NicNacPhoto from '../../../../../image_assets/pfp/nicnac.jpg'
import WanyeaPhoto from '../../../../../image_assets/pfp/wanyea.jpg'
import PatrickPhoto from '../../../../../image_assets/pfp/ryan.jpg'
import EricPhoto from '../../../../../image_assets/pfp/eric.jpg'
import DomPhoto from '../../../../../image_assets/pfp/dom.jpg'
import CharleyPhoto from '../../../../../image_assets/pfp/charley.jpg'
// import CharleyPhoto from ''
import { smallScreen } from '../../Constants'



export default function Meet() {

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
    <div className="meet">
      
      <div className="row spaced">
        <div className="col left padding">
          <h1>Wanyea Barbel</h1>
          <p>{Wanyea}</p>
        </div>
        <div className="col center">
          <img src={WanyeaPhoto} className='photo' />
        </div>
      </div>
      
      <div className={((windowSize > smallScreen) ? 'row-reverse' : 'row') + ' spaced'}>
        <div className="col left padding">
          <h1>Charley Moore</h1>
          <p>{Charley}</p>
        </div>
        <div className="col center">
          <img src={CharleyPhoto} className='photo' />
        </div>
      </div>
      
      <div className="row spaced">
        <div className="col left padding">
          <h1>Dominique "Dom" Ortega</h1>
          <p>{Dominique}</p>
        </div>
        <div className="col center">
          <img src={DomPhoto} className='photo' />
        </div>
      </div>
      
      <div className={((windowSize > smallScreen) ? 'row-reverse' : 'row') + ' spaced'}>
        <div className="col left padding">
          <h1>Glenn "Eric" Hartwell</h1>
          <p>{Eric}</p>
        </div>
        <div className="col center">
        <img src={EricPhoto} className='photo' />
        </div>
      </div>
      
      <div className="row spaced">
        <div className="col left padding">
          <h1>Patrick "Ryan" Maley</h1>
          <p>{Patrick}</p>
        </div>
        <div className="col center">
          <img src={PatrickPhoto} className='photo' />
        </div>
      </div>
      
      <div className={((windowSize > smallScreen) ? 'row-reverse' : 'row') + ' spaced'}>
        <div className="col left padding">
          <h1>Nicholas "NicNac" Yardich</h1>
          <p>{NicNac}</p>
        </div>
        <div className="col center">
          <img src={NicNacPhoto} className='photo' />
        </div>
      </div>

    </div>
  )
}
