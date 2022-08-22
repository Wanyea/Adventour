import { useState } from 'react'

import Nav from './components/nav/Nav'
import Home from './components/home/Home'
import Features from './components/features/Features'
import Download from './components/download/Download'
import Meet from './components/meet/Meet'
import Footer from './components/footer/Footer'
import Privacy from './components/privacy/Privacy'
import Terms from './components/terms/Terms'

import './css/App.css'

function App() {
  const [home, setHome] = useState(true);
  const [features, setFeatures] = useState(false);
  const [download, setDownload] = useState(false);
  const [meet, setMeet] = useState(false);
  const [privacy, setPrivacy] = useState(false);
  const [terms, setTerms] = useState(false);

  return (
    // <div className="App">
    //   <Nav />
    // </div>
    <div className='App'> 
      <Nav home={home} setHome={setHome} 
            features={features} setFeatures={setFeatures} 
            download={download} setDownload={setDownload} 
            meet={meet} setMeet={setMeet}
            privacy={privacy} setPrivacy={setPrivacy}
            terms={terms} setTerms={setTerms} />

      {home ? <Home /> : null}
      {features ? <Features /> : null}
      {download ? <Download /> : null}
      {meet ? <Meet /> : null}
      {privacy ? <Privacy /> : null}
      {terms ? <Terms /> : null}

      <Footer home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />
    </div>
  )
}

export default App
