FROM fedora:31

# install dependencies
RUN dnf update -y \
	&& dnf install vim -y \
	&& dnf install curl -y \
	&& dnf install make -y \
	&& dnf install git -y \
	&& dnf install mercurial -y \
	&& dnf install cekit -y \
	&& dnf install odcs-client -y \
	&& dnf install odcs -y \
	&& dnf install python3-odcs-client -y
	
# install go
RUN curl -O https://storage.googleapis.com/golang/go1.12.9.linux-amd64.tar.gz \
	&& tar -xvf go1.12.9.linux-amd64.tar.gz \
	&& chown -R root:root ./go \
	&& mv go /usr/local \
	&& ln /usr/local/go/bin/go /usr/local/bin
	
# install operator sdk
RUN RELEASE_VERSION=v0.11.0 \ 
	&& curl -LO https://github.com/operator-framework/operator-sdk/releases/download/${RELEASE_VERSION}/operator-sdk-${RELEASE_VERSION}-x86_64-linux-gnu \
	&& chmod +x operator-sdk-${RELEASE_VERSION}-x86_64-linux-gnu \
	&& cp operator-sdk-${RELEASE_VERSION}-x86_64-linux-gnu /usr/local/bin/operator-sdk \
	&& rm operator-sdk-${RELEASE_VERSION}-x86_64-linux-gnu
	
COPY compile.sh /
WORKDIR /