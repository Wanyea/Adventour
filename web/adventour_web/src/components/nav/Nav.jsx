import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import BigLogo from '../../../../../image_assets/Logo_Adventour_Transparent.png'
import SmallLogo from '../../../../../image_assets/Logo_Balloon_Transparent.png'
import "./Nav.css"
import "../../css/Containers.css"
import { smallScreen } from '../../Constants'

export default function Nav(props) {
  
  const classList = ['nav-list', 'top', 'middle', 'bottom', 'button', 'button-close']

  const [menuOpen, setMenuOpen] = useState(() => {
    if (window.innerWidth <= smallScreen)
      return false;
    else
      return true;
  });

  const [logo, setLogo] = useState();

  // Close menu if clicked outside of
  useEffect(() => {
    window.addEventListener('click', (e) => {
      for (const element of classList)
      {
          if (e.target.className === element || window.innerWidth >= smallScreen)
          {
            return;
          }
      }
      setMenuOpen(false);
    })
  
    return () => {
      window.removeEventListener('click', (e) => {
        if (e.target.className !== 'nav-list', e.target.className !== 'nav-list')
        {
          setMenuOpen(false);
        }
        
      })
    }
  }, [])

  useEffect(() => {
    formatNavbar();
    window.addEventListener('resize', formatNavbar)
  
    return () => {
      window.removeEventListener('resize', formatNavbar);
    }
  }, [])
  
  

  const resetAll = () => {
    props.setHome(false);
    props.setFeatures(false);
    props.setDownload(false);
    props.setMeet(false);
    props.setTerms(false);
    props.setPrivacy(false);
  }

  const formatNavbar = () => {
    if (window.innerWidth <= smallScreen)
    {
      setLogo(SmallLogo);
      setMenuOpen(false);
    }
    else
    {
      setLogo(BigLogo);
      setMenuOpen(true);
    }
  }

  const changeMenu = () => {
    setMenuOpen(!menuOpen);
  }


  return (
    <div id='nav' className='nav-bar'>
      <img className='logo' src={logo} onClick={() => {resetAll(); props.setHome(true)}}></img>
      <div className={menuOpen ? 'button-close' : 'button'} onClick={changeMenu}>
        <div className="top"></div>
        <div className="middle"></div>
        <div className="bottom"></div>
      </div>
      <div className='download'>Download</div>
      {menuOpen ? <div className='nav-list'>
        <Link to='/' className={props.home ? 'nav-list-item current' : 'nav-list-item'} onClick={() => {resetAll(); props.setHome(true)}}>What is Adventour</Link>
        <Link to='features' className={props.features ? 'nav-list-item current' : 'nav-list-item'} onClick={() => {resetAll();props.setFeatures(true)}}>Features</Link>
        <Link to='download' className={props.download ? 'nav-list-item current' : 'nav-list-item'} onClick={() => {resetAll();props.setDownload(true)}}>Download</Link>
        <Link to='meet-the-team' className={props.meet ? 'nav-list-item current' : 'nav-list-item'} onClick={() => {resetAll();props.setMeet(true)}}>Meet the Team</Link>
      </div> : null}
    </div>
  )
}
