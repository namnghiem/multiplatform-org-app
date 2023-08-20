//
//  Interactions.swift
//  SMFApp
//
//  Created by Nam Nghiem on 29/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import Foundation
import UIKit
import EventKit

class Interactions {
    
    static let weekdays: [String:Int] = ["Sunday":1, "Monday":2, "Tuesday":3, "Wednesday":4, "Thursday":5, "Friday":6, "Saturday":7]
    
    static func shareTextButton(text: String, viewController: UIViewController) {
    
    // text to share
    
    // set up activity view controller
    let textToShare = [ text ]
    let activityViewController = UIActivityViewController(activityItems: textToShare, applicationActivities: nil)
    
    // present the view controller
    viewController.present(activityViewController, animated: true, completion: nil)
    
    }
    
    static func openCalendar(with date: Date) {
        guard let url = URL(string: "calshow:\(date.timeIntervalSinceReferenceDate)") else {
            print(date)
            return
        }
        DispatchQueue.main.sync {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)

        }
    }

    static func addToCalendar(date: Date, viewController: UIViewController, title: String, notes: String, completion: ((_ success: Bool, _ error: NSError?) -> Void)? = nil){
        let eventStore = EKEventStore()
        
        eventStore.requestAccess(to: .event, completion: { (granted, error) in
            if (granted) && (error == nil) {
                let event = EKEvent(eventStore: eventStore)
                event.title = title
                event.startDate = date
                event.endDate = Calendar.current.date(byAdding: .hour, value: 1, to: date)
                event.notes = notes
                event.calendar = eventStore.defaultCalendarForNewEvents
                do {
                    try eventStore.save(event, span: .thisEvent)
                } catch let e as NSError {
                    completion?(false, e)
                    return
                }
                openCalendar(with: date)
                completion?(true, nil)
            } else {
                let alertController = UIAlertController(title: "Permissions required", message: "Trinity SMF requires the Calendar permission to add this event to your device's calendar.", preferredStyle: .alert)
                alertController.addAction(UIAlertAction(title: "OK", style: .default, handler: { action in
                    switch action.style {
                    case .default:
                        print("default")
                    case .cancel:
                        print("cancel")
                    case .destructive:
                        print("destructive")
                    }
                }))
                viewController.present(alertController, animated: true, completion:nil)

                completion?(false, error as NSError?)

            }
        })
    }
}
