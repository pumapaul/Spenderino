import Combine
import shared

func flowAsPublisher<T>(_ flow: CFlow<T>) -> AnyPublisher<T, Never> {
    return Deferred<Publishers.HandleEvents<PassthroughSubject<T, Never>>> {
        let subject = PassthroughSubject<T, Never>()
        let closable = flow.watchOnMain { next in
            if let next = next {
                subject.send(next)
            }
        }
        return subject.handleEvents(receiveCancel: {
            closable.close()
        })
    }.eraseToAnyPublisher()
}

func flowAsOptionalPublisher<T>(_ flow: CFlow<T>) -> AnyPublisher<T?, Never> {
    return Deferred<Publishers.HandleEvents<PassthroughSubject<T?, Never>>> {
        let subject = PassthroughSubject<T?, Never>()
        let closable = flow.watchOnMain { next in
            subject.send(next)
        }
        return subject.handleEvents(receiveCancel: {
            closable.close()
        })
    }.eraseToAnyPublisher()
}
