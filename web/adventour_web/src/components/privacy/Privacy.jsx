import React from 'react'
import './Privacy.css'

const Privacy = (props) => {

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
    <h1 className='pad'>How does Adventour handle privacy?</h1>
    <p className='pad'>
      We take privacy seriously at Adventour because we want our users to feel safe using our application. 
      We take pride in the fact that we will never give or sell your information to third party companies. 
      You can find the entire privacy policy below.
    </p>
    <h2 className='pad'>Our Privacy Policy</h2>
    <p className='pad'>
      As the team does not have a legal advisor for this project, this section is merely a proposed 
      Privacy Policy until we get an official version. In no way is this section legally binding while it is 
      still a work in progress.
    </p>
    <p className='double-pad bold'>
      Last updated April 23, 2022
    </p>
    <p className='pad'>
      The following Privacy Policy ("Policy," "Policies") will apply to users ("you," "your," "user") who 
      access or use Adventour's ("our," "we," "us") Service through Adventour's mobile application 
      ("Service," "Application"), whether or not the user has created their own account. These Policies will 
      inform you of what information we collect from you when you use our Service, and how we use it. If you 
      do not agree with these Policies, nor with our Terms of Service, cease your use of our Service immediately.
    </p>
    <p className='pad'>
      These Policies are subject to change. We will notify you of any updates to these Policies and our Terms of 
      Service through the Application. Continued use of our Service is a binding agreement that you agree to the 
      current and most recent Privacy Policy. The date of the latest update will be written at the beginning of 
      this page.
    </p>
    <ol className='outer'>
      <li>What information we collect and how we use it</li>
        <ol className="outer" type='a'>
          <li>Account Information</li>
            <ol className="inner" type='i'>
              <li>
                We collect any data you provide to us through your account and profile creation, such as your 
                username, email address, date of birth, profile picture, and profile information. Your email 
                address will be used for email notifications (should you opt in) or other necessary 
                communications.
              </li>
            </ol>
          <li>User Content</li>
            <ol className="inner" type='i'>
              <li>
                We will have access to any content you upload onto the application, public or private. 
                Any public information and content you provide can be accessed by any user, such as public 
                Adventours (and location information from visited Destinations), public Beacons (and photos), 
                public profiles (with profile picture, username, date of birth, and profile bio).
              </li>
            </ol>
          <li>Communications</li>
            <ol className="inner" type='i'>
              <li>
                Any information tied to any communication you have with Adventour 
                (e.g. your email address, the content of your message, time and date it was sent, etc.) 
                This could be through contacting us through our support services, reviews and feedback 
                through the App Store or Play Store, or other means of communicating with us.
              </li>
            </ol>
          <li>Activity and Cookies</li>
            <ol className="inner" type='i'>
              <li>
                We will collect data on your use of our Application. Cookies will be used per session and for 
                as long as you keep them enabled for the Application. The cookies are essential to the 
                functionality of the Application. We will also save your previous Adventours and Destination 
                choices. An editable list will be provided for Destinations that you ask us to never 
                recommend to you. We may use your activity and cookie data to enhance and personalize your 
                experience of our Service.
              </li>
            </ol>
        </ol>
      <li>Your rights and responsibilities with your privacy</li>
        <ol className="outer" type='a'>
          <li>Entering and Updating Your Information</li>
            <ol className="inner" type='i'>
              <li>
              You will have the ability to enter your personal information and edit them when needed. 
              It is your responsibility to keep this information up-to-date. 
              </li>
            </ol>
          <li>Deleting Your Account</li>
            <ol className="inner" type='i'>
              <li>
              If you no longer wish for Adventour to keep your information, we suggest deleting your account.
              </li>
            </ol>
          <li>Responsibilities with your privacy</li>
            <ol className="inner" type='i'>
              <li>
              What information you decide to disclose to us or to the public audience is up to your 
              discretion. It is your responsibility to keep private the information you do not want 
              publicized. 
              </li>
            </ol>
        </ol>
      <li>Contact Us</li>
        <ol className="inner" type='a'>
          <li>
            If you find any discrepancies with the Adventour team and our Privacy Policy, contact us at dominiqueortega@knights.ucf.edu.
          </li>
        </ol>
    </ol>
  </div>
  )
}

export default Privacy