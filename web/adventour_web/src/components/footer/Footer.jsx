import { Link } from 'react-router-dom'

import './Footer.css'

const Footer = (props) => {

  
  const resetAll = () => {
    props.setHome(false);
    props.setFeatures(false);
    props.setDownload(false);
    props.setMeet(false);
    props.setTerms(false);
    props.setPrivacy(false);
  }

  return (
    <div className='footer'>
      <div className='sections'>

        <div className="col flex top-margin">
          <h1 className='h1-left-footer'>Adventour
            <div className='subsections'>
              <Link to="/" className='link' onClick={() => {resetAll();props.setHome(true)}}>About Us</Link>
              <Link to="features" className='link' onClick={() => {resetAll();props.setFeatures(true)}}>Features</Link>
              <Link to="meet-the-team" className='link' onClick={() => {resetAll();props.setMeet(true)}}>Meet the Team</Link>
            </div>
          </h1>
        </div>

        <div className="col flex top-margin">
          <h1 className='h1-middle-footer'>Download
            <div className='subsections'>
              <Link className='link'>Android</Link>
              <Link className='link'>iOS</Link>
            </div>
          </h1>
        </div>

        <div className="col flex top-margin">
          <h1 className='h1-right-footer'>Help
            <ul className='subsections'>
              <Link to='terms-of-service' className='link' onClick={() => {resetAll();props.setTerms(true)}}>
                  Terms of Service
              </Link>
              <Link to='privacy-policy' className='link' onClick={() => {resetAll();props.setPrivacy(true)}}>
                  Privacy Policy
              </Link>
            </ul>
          </h1>
        </div> 
        
      </div>
    </div>
  )
}

export default Footer