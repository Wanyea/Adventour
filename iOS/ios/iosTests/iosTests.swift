//
//  iosTests.swift
//  iosTests
//
//  Created by Cassidy Moore on 8/23/22.
//

import XCTest
@testable import ios

class storyboardTests: XCTestCase {
    var signUpVC: SignUpViewController!
    var beaconVC: BeaconPostViewController!
    var source: UIViewController!
    var sourceNil: UIViewController!
    
    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        signUpVC = storyBoard.instantiateViewController(withIdentifier: "SignUpVC") as? SignUpViewController
        
        let sb = UIStoryboard(name: "BeaconPost", bundle: nil)
        beaconVC = sb.instantiateViewController(withIdentifier: "BeaconPost") as? BeaconPostViewController
        source = CongratsViewController()
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        signUpVC = nil
        beaconVC = nil
        source = nil
    }

    func testExample() throws {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        // Any test you write for XCTest can be annotated as throws and async.
        // Mark your test throws to produce an unexpected failure when your test encounters an uncaught error.
        // Mark your test async to allow awaiting for asynchronous code to complete. Check the results with assertions afterwards.
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    
    func testSignUpViewControllerPasswordValid() throws {
        XCTAssertTrue(signUpVC.isPasswordValid("Adventour1!"))
        XCTAssertFalse(signUpVC.isPasswordValid("adventour"))
        XCTAssertFalse(signUpVC.isPasswordValid("Adventour!"))
        XCTAssertFalse(signUpVC.isPasswordValid("adventour1!"))
    }

    func testBeaconPostAlert() throws {
        XCTAssert(beaconVC.getAlertTitle() == "Save Beacon")
        XCTAssert(beaconVC.getButtonTitle() == "Save")
        XCTAssert(beaconVC.getAlertMessage() == "Are you sure you are ready to save your Beacon?")
    }
}
