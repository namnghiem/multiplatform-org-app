
//  NewsViewController.swift
//
//
//  Created by Nam Nghiem on 28/08/2019.
//

import UIKit

import UIKit
import Alamofire
import SafariServices
import SwiftyJSON
import Kingfisher

class NewsViewController: UIViewController ,UITableViewDelegate,UITableViewDataSource {
    
    @IBOutlet weak var newsTable: UITableView!
    
    @IBOutlet weak var headerCell: UIView!
    
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    var events: [Event] = []

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count
        
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = newsTable.dequeueReusableCell(withIdentifier: "eventsId", for: indexPath) as! EventsTableViewCell
        
        let event = events[indexPath.row]
        cell.eventAuthor.text = event.author
        cell.eventDescription.text = event.description.replacingOccurrences(of: "<[^>]+>", with: "", options: String.CompareOptions.regularExpression, range: nil)
        cell.eventTime.text = event.time
        cell.eventVenue.text = event.venue
        cell.eventTitle.text = event.title
        
        cell.selectionStyle = .none
        
        if event.imageUrl != "null"{
            if let url = URL(string: event.imageUrl)  {
                cell.eventImage.kf.setImage(with: url)
            }
            //cell.eventImage.layer.cornerRadius = 16.0
        }
        // add code to download the image from fruit.imageURL
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let url = URL(string: events[indexPath.row].link) else { return }
        let svc = SFSafariViewController(url: url)
        present(svc, animated: true, completion: nil)
    }
    

    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "News"
        // Do any additional setup after loading the view.
        newsTable.delegate = self
        newsTable.dataSource = self
        activityIndicator.hidesWhenStopped = true
        headerCell.layer.cornerRadius = 17.0
        activityIndicator.startAnimating()
    AF.request("https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=beba570f76094a808fdb7bf78bf1dbbc", method: .get, parameters: nil, encoding: JSONEncoding.default)
        .responseJSON { response in
            //to get JSON return value
            do{
                let json = JSON(response.data!)
                print (json)
                print (json["articles"].count)
                for x in (0...json["articles"].count - 1){
                    
                    
                    let author = json["articles"][x]["author"].string ?? "Author"
                    
                    let description = json["articles"][x]["description"].string ?? "Tap to read more"
                    
                    let time = json["articles"][x]["publishedAt"].string ?? "Time"
                    
                    let venue = json["articles"][x]["source"]["name"].string ?? "SMF News"
                    
                    let title = json["articles"][x]["title"].string ?? "Title"
                    
                    let pictureUrl = json["articles"][x]["urlToImage"].string ?? ""
                    
                    let link = json ["articles"][x]["url"].string ?? "http://trinitysmf.com"
                    
                    let event = Event(title:title, author:author, venue:venue, time:time, description:description, imageUrl: pictureUrl, link:link)
                    
                    self.events.append(event)
                    
                }
                self.newsTable.reloadData()
                self.newsTable.isHidden = false
                self.activityIndicator.stopAnimating()
            }catch let parsingError {
                print("Error", parsingError)
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
}
