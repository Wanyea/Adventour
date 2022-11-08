//
//  ProfilePicViewController.swift
//  ios
//
//  Created by Wanyea on 10/31/22.
//

import UIKit

class ProfilePicViewController: UIViewController {
    weak var delegate: ModalViewControllerDelegate?
    
    @IBOutlet weak var profileCheeta: profilePicImageView!
    @IBOutlet weak var profileElephant: profilePicImageView!
    @IBOutlet weak var profileFox: profilePicImageView!
    
    @IBOutlet weak var profileBug: profilePicImageView!
    @IBOutlet weak var profileMonkey: profilePicImageView!
    @IBOutlet weak var profilePenguin: profilePicImageView!
    
    

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let tap = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap2 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap3 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap4 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap5 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap6 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        profileCheeta?.isUserInteractionEnabled = true
        profileCheeta?.iosPfpRef = "profpic_cheeta"
        profileCheeta?.addGestureRecognizer(tap)
        profileElephant?.isUserInteractionEnabled = true
        profileElephant?.iosPfpRef = "profpic_elephant"
        profileElephant?.addGestureRecognizer(tap2)
        profileFox?.isUserInteractionEnabled = true
        profileFox?.iosPfpRef = "profpic_Fox"
        profileFox?.addGestureRecognizer(tap3)
        profileBug?.isUserInteractionEnabled = true
        profileBug?.iosPfpRef = "profpic_ladybug"
        profileBug?.addGestureRecognizer(tap4)
        profileMonkey?.isUserInteractionEnabled = true
        profileMonkey?.iosPfpRef = "profpic_monkey"
        profileMonkey?.addGestureRecognizer(tap5)
        profilePenguin?.isUserInteractionEnabled = true
        profilePenguin?.iosPfpRef = "profpic_penguin"
        profilePenguin?.addGestureRecognizer(tap6)
        
    }
    
    
    @objc func tapDetected(sender: UITapGestureRecognizer){
        
        //picture = self.image
        
        let view = sender.view as! profilePicImageView
        signupData["iosPfpRef"] = view.iosPfpRef
        
        picture = view.image
        picFlag = 1
        if let delegate = delegate {
            delegate.modalControllerWillDisappear()
        }
        self.dismiss(animated: true, completion: nil)

    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destinationVC = segue.destination as? SignUpViewController{
            print("1")
            if(picFlag == 0)
                    {
                destinationVC.profilePic?.image = UIImage(systemName: "questionmark")
                    }
            else{
                destinationVC.profilePic?.image = picture
            }
        }
        
        
        
        
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    

}

protocol ModalViewControllerDelegate: NSObjectProtocol{
    func modalControllerWillDisappear()
}

final class ModalViewController: UIViewController{
    weak var delegate: ModalViewControllerDelegate?
}

