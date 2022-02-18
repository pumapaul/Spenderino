//
//  LoadingButton.swift
//  Spenderino
//
//  Created by Paul Weber on 18.02.22.
//

import SwiftUI

struct LoadingButton<Label: View>: View {
    let isLoading: Bool
    let action: () -> Void
    let label: () -> Label
    
    var body: some View {
        Button {
            action()
        } label: {
            if isLoading {
                ProgressView()
                    .frame(minWidth: 200, minHeight: 30)
            } else {
                label()
                    .frame(minWidth: 200, minHeight: 30)
            }
        }
    }
}

