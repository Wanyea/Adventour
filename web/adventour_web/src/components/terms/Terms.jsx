import React from 'react'
import './Privacy.css'

const Terms = (props) => {

  const resetAll = () => {
    props.setHome(false);
    props.setFeatures(false);
    props.setDownload(false);
    props.setMeet(false);
    props.setTerms(false);
    props.setPrivacy(false);
  }
  resetAll();

  return (
  <div className="container privacy">
    <h1 className='pad'>A safe community is important to us.</h1>
    <p className='pad'>
      We want Adventour to be a place where everyone feels like they belong. To help create and facilitate a strong community we have laid out some rules that everyone needs to follow to keep Adventour safe and free of discrimation and hate. You can find our Terms of Service below.
    </p>
    <h2 className='pad'>Terms of Service</h2>
    <p className='pad'>
      As the team does not have a legal advisor for this project, this section is merely a proposed Terms of Service 
      until we get an official version. In no way is this section legally binding while it is still a work in progress.
    </p>
    <p className='double-pad bold'>
      Last updated April 23, 2022
    </p>
    <p className='pad'>
      The following Terms of Service ("Terms'') will apply to users ("you," "your," "user") who access or use 
      Adventour's ("our," "we," "us") Service through Adventour's mobile application ("Service," "Application"), 
      whether or not the user has created their own account. These Terms govern your use of our Service. If you do 
      not agree with these Terms, nor with our Privacy Policy, cease your use of our Service immediately.
    </p>
    <p className='pad'>
      These Terms are subject to change. We will notify you of any updates to these Terms and our Privacy Policy 
      through the Application. Continued use of our Service is a binding agreement that you agree to the current and 
      most recent Terms. The date of the latest update will be written at the beginning of this page.
    </p>
    <ol className='outer'>
      <li>Use of the Service</li>
        <ol className="outer" type='a'>
          <li>Accounts</li>
            <ol className="inner" type='i'>
              <li>
                To use our Service, you must create an account. When creating your account and when using our Service, 
                all of your information must be accurate and current. You are not allowed to pretend to be someone you 
                are not, nor are you allowed to impersonate another person without authorization.
              </li>
            </ol>
          <li>Sensitive or disturbing information.</li>
            <ol className="inner" type='i'>
              <li>
                You are not allowed to enter anything vulgar, obscene, profane, nor offensive, as deemed by Adventour's 
                staff or moderation team, to your private or public information
              </li>
            </ol>
          <li>Age of user</li>
            <ol className="inner" type='i'>
              <li>
                To use our Service, you must be at least 13 years old. We do not condone any user to visit a 
                destination they are too young to visit. We do not condone minors to visit destinations without 
                adult guidance.
              </li>
            </ol>
          <li>Confidentiality</li>
            <ol className="inner" type='i'>
              <li>
                The user is responsible for keeping their private information (e.g. email, password, name, location) 
                safe. Disclosure of any user information is up to the user.
              </li>
            </ol>
          <li>Unlawful acts</li>
            <ol className="inner" type='i'>
                <li>
                  You are not allowed to use our Service for any unlawful, illegal or harmful purposes, 
                  including but not limited to:
                </li>
                <ol className="inner left-list" type='A'>
                  <li>defamatory or libelous speech;</li>
                  <li>promoting violence, hateful, or abusive acts; and</li>  
                  <li>stalking or harassment.</li>
                </ol>
              
            </ol>
        </ol>
      <li>Content. <span className='light'>"Content" may refer to any public information viewed by the users, including information provided by the users themselves.</span> </li>
        <ol className="outer" type='a'>
          <li>Responsibility</li>
            <ol className="inner" type='i'>
              <li>
                The user may be subject to viewing Content that is sensitive or disturbing to them. 
                If you believe that a Content is breaking our Terms, contact us immediately. 
              </li>
            </ol>
          <li>Sensitive or disturbing Content</li>
            <ol className="inner" type='i'>
              <li>
                You are not allowed to enter anything vulgar, obscene, profane, nor offensive, as deemed by Adventour's 
                staff or moderation team, to your public Content (e.g. photos, text, etc.)
              </li>
            </ol>
          <li>Ownership</li>
            <ol className="inner" type='i'>
              <li>
                Any Content you create within or in relation to Adventour (within or outside of the application) 
                is your own. We are not obligated to take down content any user decides to upload to the Application 
                unless it breaks our Terms. We are not liable for any harm your Content may cause.
              </li>
            </ol>
          <li>Content accuracy</li>
            <ol className="inner" type='i'>
                <li>
                  We try our very best to provide you with the most recent and up-to-date information for each 
                  Destination ("Destination" refers to any business, location, activity, etc. listed in our 
                  Application.) We are not responsible for any inaccuracies for a listed Destination, as we are not 
                  official representatives of any Destination. If you spot any inaccuracies, please contact us 
                  immediately.
                </li>
            </ol>
        </ol>
      <li>Suspension</li>
        <ol className="inner" type='a'>
          <li>
            Any user found breaking these Terms will be suspended immediately. If you believe your account has been 
            wrongfully terminated, contact us immediately.
          </li>
        </ol>
      <li>Contact us</li>
        <ol className="inner" type='a'>
          <li>
            If you find any user to break these Terms of Service, or if you find any discrepancies with the 
            Adventour team and our enforcement of these Terms, contact us at dominiqueortega@knights.ucf.edu.
          </li>
        </ol>
    </ol>
  </div>
  )
}

export default Terms