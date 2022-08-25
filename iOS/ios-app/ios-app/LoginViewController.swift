//
//  LoginViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/24/22.
//

import UIKit


class LoginViewController: UIViewController {
    
   
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    


    @IBAction func loginTapped(_ sender: Any) {
        
                // ...
                // after login is done, maybe put this in the login web service completion block

                let storyboard = UIStoryboard(name: "Main", bundle: nil)
               
                let mainTabBarController = storyboard.instantiateViewController(identifier: "MainTabBarController")
                
                // This is to get the SceneDelegate object from your view controller
                // then call the change root view controller function to change to main tab bar
                (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
            }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }
}


