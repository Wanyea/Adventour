import { useState } from 'react'
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";

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
      <Router>
        <Nav home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />
              
        <Routes>
          <Route path="/" element={<Home home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />} />
          <Route path="/features" element={<Features home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />} />
          <Route path="/download" element={<Download home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />} />
          <Route path="/meet-the-team" element={<Meet home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />} />
          <Route path="/privacy-policy" element={<Privacy home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />} />
          <Route path="/terms-of-service" element={<Terms home={home} setHome={setHome} 
              features={features} setFeatures={setFeatures} 
              download={download} setDownload={setDownload} 
              meet={meet} setMeet={setMeet}
              privacy={privacy} setPrivacy={setPrivacy}
              terms={terms} setTerms={setTerms} />} />
        </Routes>

        <Footer home={home} setHome={setHome} 
                features={features} setFeatures={setFeatures} 
                download={download} setDownload={setDownload} 
                meet={meet} setMeet={setMeet}
                privacy={privacy} setPrivacy={setPrivacy}
                terms={terms} setTerms={setTerms} />
      </Router>
    </div>
  )
}

export default App
