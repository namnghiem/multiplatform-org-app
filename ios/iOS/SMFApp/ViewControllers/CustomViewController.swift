//
//  CustomViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 05/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import Foundation
import FirebaseUI
import UIKit

class CustomAuthPickerViewController: FUIAuthPickerViewController{
    var reachability: Reachability?
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let width = UIScreen.main.bounds.size.width
        let height = UIScreen.main.bounds.size.height
        
        let logoImage = #imageLiteral(resourceName: "logo")
        
        let imageViewBackground = UIImageView(frame: CGRect(x:0, y:0, width:width, height:300))
        imageViewBackground.image = logoImage
        imageViewBackground.contentMode = UIView.ContentMode.scaleAspectFit
        view.subviews[0].addSubview(imageViewBackground)
 imageViewBackground.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        
        
        //label
        let label = UILabel(frame:CGRect(x:0, y:300, width:width, height:30))
        
        label.translatesAutoresizingMaskIntoConstraints = false
        
        label.text = "Log In or Create an Account to Continue"
        view.subviews[0].insertSubview(label,at: 2)
        
        label.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        label.topAnchor.constraint(equalTo: imageViewBackground.bottomAnchor).isActive = true
        label.textAlignment = .center
        
        self.navigationItem.leftBarButtonItem = nil
        do{
            reachability = try Reachability()
        
            reachability!.whenUnreachable = { _ in
                print("Not reachable")
                let alert = UIAlertController(title: "No internet connection", message: "Please check your WI-FI or 4G connection to continue", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
                self.present(alert, animated: true, completion: nil)
            
            }
            
        
            do {
                try reachability!.startNotifier()
            } catch {
                print("Unable to start notifier")
            }
        }catch let exception{
            print(exception)
        }
    }
}
