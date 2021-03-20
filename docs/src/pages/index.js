import React from 'react';
import clsx from 'clsx';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

const features = [
  {
    title: 'Faceted World Generation',
    imageUrl: 'img/logo-facets.svg',
    description: (
      <>
        Build up information about the landscape and its features
        from multiple facets. Like a cut gem, each facet is one
        side of a many-sided process. 
        Facet providers fro a graph allowing for reuse and 
        compositon. From dense height maps to sparse object 
        configurations - the choice is yours!
      </>
    ),
  },
  {
    title: 'World Rasterizers',
    imageUrl: 'img/logo-rasterizer.svg',
    description: (
      <>
        The tools to turn abstract ideas into blocks in the world.
        Each rasterizer interprets facet data to decide on the 
        type and shape of a block to place. All rasterizers of
        a world generation setup work together in shaping the world 
        the player is exploring.
      </>
    ),
  },
  {
    title: 'Modular and Extensible',
    imageUrl: 'img/logo-modular.svg',
    description: (
      <>
        Terasology's modular design also applies to the world
        generation framework. Modules can implement both 
        facet providers and rasterizers to customize the 
        creation process. 
        
        For even more flexibility, world generation allows to
        plug in extensions to bring unique features such as lakes,
        caves, or vaolcanoes to the game.        
      </>
    ),
  },
];

function Feature({imageUrl, title, description}) {
  const imgUrl = useBaseUrl(imageUrl);
  return (
    <div className={clsx('col col--4', styles.feature)}>
      {imgUrl && (
        <div className="text--center">
          <img className={styles.featureImage} src={imgUrl} alt={title} />
        </div>
      )}
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
}

export default function Home() {
  const context = useDocusaurusContext();
  const {siteConfig = {}} = context;
  return (
    <Layout
      title={siteConfig.title}
      description={siteConfig.tagline}>
      <header className={clsx('hero hero--primary', styles.heroBanner)}>
        <div className="container">
          <h1 className="hero__title">{siteConfig.title}</h1>
          <p className="hero__subtitle">{siteConfig.tagline}</p>
          <div className={styles.buttons}>
            <Link
              className={clsx(
                'button button--outline button--secondary button--lg',
                styles.getStarted,
              )}
              to={useBaseUrl('docs/')}>
              Get Started
            </Link>
          </div>
        </div>
      </header>
      <main>
        {features && features.length > 0 && (
          <section className={styles.features}>
            <div className="container">
              <div className="row">
                {features.map((props, idx) => (
                  <Feature key={idx} {...props} />
                ))}
              </div>
            </div>
          </section>
        )}
      </main>
    </Layout>
  );
}
