<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<a name="readme-top"></a>

<!-- PROJECT SHIELDS -->

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url] [![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache 2.0][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- TABLE OF CONTENTS -->
<details>
   <summary>Table of Contents</summary>
   <ol>
      <li><a href="#about-the-project">About The Project</a></li>
      <li><a href="#our-ods">Open Data Space</a></li>
      <li><a href="#our-eds-extensions">Open Data Space Extensions</a></li>
      <li><a href="#compatibility">Compatibility</a></li>
      <li><a href="#getting-started">Getting Started</a></li>
      <li><a href="#contributing">Contributing</a></li>
      <li><a href="#license">License</a></li>
      <li><a href="#contact">Contact</a></li>
   </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

[Eclipse Dataspace Components](https://github.com/eclipse-edc) (EDC) is a framework
for building dataspaces, exchanging data securely with ensured data sovereignty.

[Utopia IA](https://utopiaia.com/) extends the EDC Connector's functionality with extensions to offer
enterprise-ready managed services like "Connector-as-a-Service", out-of-the-box fully configured DAPS
and integrations to existing other dataspace technologies. Based on the Sovity EDC CE. Thanks Sovity!

This repository contains our Utopia IA OpenDataSpace, containing pre-configured open source EDC extensions.

Check out our [Getting Started Section](#getting-started) on how to run a local Open Data Space.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- COMPATIBILITY -->

## OpenDataSpace

Our OpenDataSpace takes available Open Source EDC extensions and combines them with our own
open source EDC extensions from this repository to build ready-to-use EDC Docker Images.

See [here](launchers/README.md) for a list of our OpenDataSpace Docker images.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## OpenDataSpace Extensions

Feel free to explore and use our [ODS extensions](./extensions) with your ODS setup.

We packaged critical extensions for compatibility with our ODS Manager and general usability features into
[sovity EDC Extensions Package](./extensions/sovity-edc-extensions-package).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Compatibility

Our OpenDataSpace and it's extensions are targeted to run with
our [utopia-ia/ods-manager](https://github.com/utopia-ia/ods-manager).

Our OpenDataSpace will use the current ODS Milestone with a certain delay
to ensure reliability with a new release. Earlier releases currently are not supported, but will be
supported, once the base ODS has a reliable version.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

The fastest way to get started is our [Getting Started Guide](docs/getting-started/README.md)
which takes you through the steps of either starting a local [docker-compose.yaml](docker-compose.yaml) or deploying a
productive Utopia IA OpenDataSpace or MDS ODS CE Connector.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to
learn, inspire, and create. Any contributions you make are **greatly
appreciated**.

If you have a suggestion that would improve this project, please fork the repo and
create a pull request. You can also simply open
a [feature request](https://github.com/utopia-ia/opendataspace/issues/new?template=feature_request.md). Don't forget to
leave the project a ⭐, if you like the effort put into this version!

Our contribution guideline can be found in [CONTRIBUTING.md](CONTRIBUTING.md).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Development

### Developer Documentation

- Local Development Guide (further below)
- [IntelliJ](docs/dev/intellij/intelliJ.md)
- [Changelog Updates](docs/dev/changelog_updates.md)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Requirements

- Docker Environment
- JDK 17
- GitHub Maven Registry Access

To access the GitHub Maven Registry you need to provide the following properties, e.g. by providing
a `~/.gradle/gradle.properties`.

```properties
gpr.user={your github username}
gpr.key={your github pat with packages.read}
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

hello@utopiaia.com

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]:
https://img.shields.io/github/contributors/utopia-ia/opendataspace.svg?style=for-the-badge

[contributors-url]: https://github.com/utopia-ia/opendataspace/graphs/contributors

[forks-shield]:
https://img.shields.io/github/forks/utopia-ia/opendataspace.svg?style=for-the-badge

[forks-url]: https://github.com/utopia-ia/opendataspace/network/members

[stars-shield]:
https://img.shields.io/github/stars/utopia-ia/opendataspace.svg?style=for-the-badge

[stars-url]: https://github.com/utopia-ia/opendataspace/stargazers

[issues-shield]:
https://img.shields.io/github/issues/utopia-ia/opendataspace.svg?style=for-the-badge

[issues-url]: https://github.com/utopia-ia/opendataspace/issues

[license-shield]:
https://img.shields.io/github/license/utopia-ia/opendataspace.svg?style=for-the-badge

[license-url]: https://github.com/utopia-ia/opendataspace/blob/main/LICENSE

[linkedin-shield]:
https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://www.linkedin.com/company/utopia-ia
