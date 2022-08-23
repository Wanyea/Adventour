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
            <ul className='subsections'>
              <li onClick={() => {resetAll();props.setHome(true)}}>
                About us
              </li>
              <li onClick={() => {resetAll();props.setFeatures(true)}}>
                Features
              </li>
              <li onClick={() => {resetAll();props.setMeet(true)}}>
                Meet the Team
              </li>
            </ul>
          </h1>
        </div>

        <div className="col flex top-margin">
          <h1 className='h1-middle-footer'>Download
            <ul className='subsections'>
              <li>
                  Android
              </li>
              <li>
                  iOS
              </li>
            </ul>
          </h1>
        </div>

        <div className="col flex top-margin">
          <h1 className='h1-right-footer'>Help
            <ul className='subsections'>
              <li onClick={() => {resetAll();props.setTerms(true)}}>
                  Terms of Service
              </li>
              <li onClick={() => {resetAll();props.setPrivacy(true)}}>
                  Privacy Policy
              </li>
            </ul>
          </h1>
        </div> 
        
      </div>
    </div>
  )
}

export default Footer