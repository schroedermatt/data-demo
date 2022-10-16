# MOCKDATA

The domain of this project includes

<object data="https://github.com/schroedermatt/data-demo/blob/main/mockdata/mockdata-erd.pdf" type="application/pdf" width="700px" height="700px">
    <embed src="https://github.com/schroedermatt/data-demo/blob/main/mockdata/mockdata-erd.pdf">
        <p>This browser does not support PDFs. Please download the PDF to view it: <a href="https://github.com/schroedermatt/data-demo/blob/main/mockdata/mockdata-erd.pdf">Download PDF</a>.</p>
    </embed>
</object>

### ERD

The ERD is generated using [erd](https://github.com/BurntSushi/erd).

To generate a new version of the diagram,

1) Clone `erd`
   - `git clone git://github.com/BurntSushi/erd`
2) Build the `erd` Docker Image
   - `erdtag="0.2.1.0"; cd erd && docker build -t erd:$erdtag . && docker run -it erd:$erdtag "--help"`
3) Build the `erd` PDF, `./gradlew generateERD`

There are other routes to installing and running the tool. See the [readme](https://github.com/BurntSushi/erd) for more details.

#### ERD Example

This example shows system and styling 
https://github.com/BurntSushi/erd/blob/master/examples/nfldb.er