//
//  AdventourSummaryNavigationController.swift
//  ios
//
//  Created by Glenn Hartwell on 10/19/22.
//

import UIKit

class AdventourSummaryNavigationController: UINavigationController {

    var locations: [String]!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print(locations)
        if let vc = self.viewControllers as? AdventourSummaryViewController {
            vc.ids = self.locations
        }
        // Do any additional setup after loading the view.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        print("Preparing...")
        if let dest = segue.destination as? AdventourSummaryViewController {
            print("IN PREPARE")
            dest.ids = self.locations
        }
    }
    /*
     MARK: - Navigation

     In a storyboard-based application, you will often want to do a little preparation before navigation
    
    */

}
