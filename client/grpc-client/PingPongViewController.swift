//
//  PingPongViewController.swift
//  grpc-client
//
//  Created by Markus Jura on 05.09.17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import UIKit

class PingPongViewController: UIViewController {

    private var streamer: BidirectionalStreamer?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.streamer = BidirectionalStreamer()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func startPressed(_ sender: Any) {
        streamer?.start()
    }

    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
