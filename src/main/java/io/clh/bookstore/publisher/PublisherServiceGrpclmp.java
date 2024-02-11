package io.clh.bookstore.publisher;
import io.clh.models.Publisher;
import io.grpc.stub.StreamObserver;
import java.util.List;
public class PublisherServiceGrpclmp extends PublisherServiceGrpc.PublisherServiceImplBase {
    private final PublisherService publisherService;

    public PublisherServiceGrpclmp(PublisherService publisherService){
        this.publisherService = publisherService;

    }
    @Override
    public void createPublisher(CreatePublisherRequest request,
                                StreamObserver<PublisherEntity> responseObserver)
    {
        try {
            Publisher publisher = new Publisher();
            publisher.setName(request.getName());
            publisher.setLocation(request.getLocation());

            Publisher createdPublisher = publisherService.addPublisher(publisher);

            PublisherEntity response = PublisherEntity.newBuilder()
                    .setPublisherId(createdPublisher.getPublisher_id())
                    .setName(createdPublisher.getName())
                    .setLocation(createdPublisher.getLocation())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e){
            responseObserver.onError(e);


        }
    }
}
