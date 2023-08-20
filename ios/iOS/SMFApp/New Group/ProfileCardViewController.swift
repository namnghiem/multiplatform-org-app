//
//  ProfileCardViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 23/09/2018.
//  Copyright © 2018 Nam Nghiem. All rights reserved.
//

import Foundation
import UIKit

class ProfileCardViewController: UIViewController{
    
    override func viewDidLoad(){
        view.layer.cornerRadius = 20.0
        view.layer.shadowColor = UIColor.gray.cgColor
        view.layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
        view.layer.shadowRadius = 12.0
        view.layer.shadowOpacity = 0.7
    }
}
