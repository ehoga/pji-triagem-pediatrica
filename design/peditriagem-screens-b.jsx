/* PediTriagem — quiz, result, orientations, history, profile, about */

const FEVER_QUIZ = [
  {
    q: 'Qual a temperatura medida agora?',
    sub: 'Termômetro axilar é o mais comum.',
    type: 'options',
    options: [
      { id: 't1', label: 'Abaixo de 37,8°C', risk: 0 },
      { id: 't2', label: 'Entre 37,8 e 38,5°C', risk: 1 },
      { id: 't3', label: 'Entre 38,5 e 39,5°C', risk: 2 },
      { id: 't4', label: 'Acima de 39,5°C', risk: 3 },
    ],
  },
  {
    q: 'Há quanto tempo a febre começou?',
    type: 'options',
    options: [
      { id: 'd1', label: 'Menos de 24 horas', risk: 0 },
      { id: 'd2', label: '1 a 3 dias', risk: 1 },
      { id: 'd3', label: 'Mais de 3 dias', risk: 2 },
    ],
  },
  {
    q: 'A criança está prostrada ou muito sonolenta?',
    sub: 'Difícil de acordar, sem energia para brincar.',
    type: 'yesno',
    weights: { yes: 3, no: 0, dunno: 1 },
  },
  {
    q: 'Há dificuldade para respirar?',
    sub: 'Respiração rápida, ofegante ou ruidosa.',
    type: 'yesno',
    weights: { yes: 3, no: 0, dunno: 2 },
  },
  {
    q: 'A criança está bebendo líquidos normalmente?',
    type: 'yesno',
    weights: { yes: 0, no: 2, dunno: 1 },
  },
  {
    q: 'Apareceram manchas na pele que não somem ao apertar?',
    sub: 'Pequenas pintinhas vermelhas ou roxas.',
    type: 'yesno',
    weights: { yes: 3, no: 0, dunno: 1 },
  },
];

// ─────────────────────────────────────────────────────────────
// Quiz
// ─────────────────────────────────────────────────────────────
function QuizScreen({ go, child, symptom, setResult, forceRisk }) {
  const [step, setStep] = React.useState(0);
  const [answers, setAnswers] = React.useState({});
  const total = FEVER_QUIZ.length;
  const cur = FEVER_QUIZ[step];

  const selectAnswer = (val) => {
    const next = { ...answers, [step]: val };
    setAnswers(next);
    setTimeout(() => {
      if (step + 1 < total) setStep(step + 1);
      else {
        // Compute risk
        let score = 0;
        FEVER_QUIZ.forEach((qq, i) => {
          const a = next[i];
          if (!a) return;
          if (qq.type === 'options') {
            const opt = qq.options.find(o => o.id === a);
            score += opt?.risk || 0;
          } else {
            score += qq.weights[a] || 0;
          }
        });
        let risk = 'low';
        if (score >= 6) risk = 'high';
        else if (score >= 3) risk = 'mod';
        if (forceRisk) risk = forceRisk;
        setResult({ risk, score, symptom, child });
        go('result');
      }
    }, 200);
  };

  const sel = answers[step];

  return (
    <div style={{ paddingBottom: 100, display: 'flex', flexDirection: 'column', height: '100%' }}>
      <ScreenHeader
        title={symptom?.name || 'Triagem'}
        subtitle={child ? `${child.name} · ${child.age}` : null}
        onBack={() => step > 0 ? setStep(step - 1) : go('symptoms')}
      />
      <ProgressBar value={step + 1} total={total} />

      <div style={{ flex: 1, padding: '4px 20px 0' }}>
        {/* Conversational bubble */}
        <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start', marginBottom: 18 }}>
          <Mascot size={44} mood="calm" />
          <div style={{
            background: '#fff', borderRadius: '4px 18px 18px 18px',
            padding: '12px 14px', boxShadow: PT.shadow, flex: 1,
          }}>
            <div style={{ fontSize: 15, fontWeight: 800, color: PT.text, lineHeight: 1.35 }}>
              {cur.q}
            </div>
            {cur.sub && <div style={{ fontSize: 12, color: PT.textMuted, marginTop: 6, fontWeight: 600 }}>{cur.sub}</div>}
          </div>
        </div>

        {cur.type === 'options' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {cur.options.map(o => {
              const isSel = sel === o.id;
              return (
                <button key={o.id} onClick={() => selectAnswer(o.id)} style={{
                  textAlign: 'left', padding: '16px 18px', borderRadius: 16, border: 'none',
                  background: isSel ? PT.primary : '#fff',
                  color: isSel ? '#fff' : PT.text,
                  boxShadow: isSel ? `0 10px 22px ${PT.primary}40` : PT.shadow,
                  fontSize: 14, fontWeight: 700, cursor: 'pointer', fontFamily: PT.font,
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  transition: 'all .2s',
                }}>
                  <span>{o.label}</span>
                  {isSel && <Icon.check size={18} />}
                </button>
              );
            })}
          </div>
        )}

        {cur.type === 'yesno' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
            {[
              { id: 'yes', label: 'Sim' },
              { id: 'no', label: 'Não' },
            ].map(o => {
              const isSel = sel === o.id;
              return (
                <button key={o.id} onClick={() => selectAnswer(o.id)} style={{
                  padding: '20px 16px', borderRadius: 16, border: 'none',
                  background: isSel ? PT.primary : '#fff',
                  color: isSel ? '#fff' : PT.text,
                  boxShadow: isSel ? `0 10px 22px ${PT.primary}40` : PT.shadow,
                  fontSize: 15, fontWeight: 800, cursor: 'pointer', fontFamily: PT.font,
                }}>{o.label}</button>
              );
            })}
            <button onClick={() => selectAnswer('dunno')} style={{
              gridColumn: 'span 2', padding: '14px', borderRadius: 14, border: `1.5px solid ${PT.hairline}`,
              background: sel === 'dunno' ? PT.navy : 'transparent',
              color: sel === 'dunno' ? '#fff' : PT.textMuted,
              fontSize: 13, fontWeight: 700, cursor: 'pointer', fontFamily: PT.font,
            }}>Não tenho certeza</button>
          </div>
        )}
      </div>

      <div style={{ padding: '12px 20px 16px' }}>
        <div style={{ fontSize: 11, color: PT.textSubtle, textAlign: 'center', fontWeight: 600 }}>
          🔒 Suas respostas ficam apenas neste dispositivo.
        </div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Result
// ─────────────────────────────────────────────────────────────
const RISK_DATA = {
  low: {
    title: 'Observar em casa',
    msg: 'No momento, os sinais informados parecem leves. Acompanhe a evolução e siga as orientações.',
    cta: 'Ver cuidados em casa',
    solid: PT.lowSolid, soft: PT.lowSoft, softer: PT.lowSofter,
    pill: 'Baixo risco', mood: 'calm',
    icon: Icon.check,
    chips: ['Hidratação', 'Repouso', 'Reavaliar em 6h'],
  },
  mod: {
    title: 'Procure avaliação médica em até 24h',
    msg: 'A criança apresenta sinais que precisam de avaliação profissional nas próximas horas.',
    cta: 'Ver cuidados até a consulta',
    solid: PT.modSolid, soft: PT.modSoft, softer: PT.modSofter,
    pill: 'Risco moderado', mood: 'watch',
    icon: Icon.warn,
    chips: ['Agendar consulta', 'Monitorar febre', 'Hidratação reforçada'],
  },
  high: {
    title: 'Procure emergência imediatamente',
    msg: 'Os sinais informados indicam necessidade de atendimento médico urgente.',
    cta: 'Ver orientações imediatas',
    solid: PT.highSolid, soft: PT.highSoft, softer: PT.highSofter,
    pill: 'Alto risco', mood: 'alert',
    icon: Icon.alert,
    chips: ['Não dar comida ou líquido', 'Manter aquecida', 'Levar à UPA mais próxima'],
  },
};

function ResultScreen({ go, result }) {
  const r = result?.risk || 'low';
  const D = RISK_DATA[r];
  const I = D.icon;
  return (
    <div style={{ paddingBottom: 32 }}>
      <ScreenHeader title="Resultado da triagem" onBack={() => go('home')} />

      <div style={{ padding: '0 20px' }}>
        {/* Hero result */}
        <div style={{
          background: `linear-gradient(180deg, ${D.softer}, #fff)`,
          borderRadius: 24, padding: 22, border: `1px solid ${D.solid}33`,
          textAlign: 'center', marginBottom: 14,
        }}>
          <div style={{
            width: 80, height: 80, borderRadius: 28, margin: '0 auto 14px',
            background: D.soft, color: D.solid,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: `0 14px 30px ${D.solid}33`,
          }}><I size={42} sw={2.4} /></div>

          <Pill tone={r === 'low' ? 'low' : r === 'mod' ? 'mod' : 'high'} style={{ marginBottom: 10 }}>
            <span style={{ width: 6, height: 6, borderRadius: '50%', background: D.solid, display: 'inline-block' }} />
            {D.pill}
          </Pill>

          <div style={{
            fontSize: 22, fontWeight: 800, color: PT.text, lineHeight: 1.2, marginBottom: 8,
          }}>{D.title}</div>
          <div style={{ fontSize: 13, color: PT.textMuted, lineHeight: 1.5, fontWeight: 600, padding: '0 6px' }}>
            {D.msg}
          </div>
        </div>

        {/* Quick chips */}
        <div style={{ marginBottom: 16 }}>
          <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>
            O que fazer agora
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {D.chips.map((c, i) => (
              <Card key={i} padding={12} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{
                  width: 28, height: 28, borderRadius: 8, background: D.softer, color: D.solid,
                  display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 12, fontWeight: 800,
                }}>{i + 1}</div>
                <div style={{ fontSize: 13, fontWeight: 700, color: PT.text }}>{c}</div>
              </Card>
            ))}
          </div>
        </div>

        {/* Summary */}
        <Card padding={14} style={{ marginBottom: 14 }}>
          <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>
            Resumo da triagem
          </div>
          <Row k="Criança" v={`${result?.child?.name || 'Maria'}, ${result?.child?.age || '4 anos'}`} />
          <Row k="Sintoma principal" v={result?.symptom?.name || 'Febre'} />
          <Row k="Data" v="04 mai 2026, 14:32" />
          <Row k="Pontuação" v={`${result?.score ?? 2} / 18`} last />
        </Card>

        {/* CTAs */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <PrimaryButton tone={r === 'high' ? 'high' : r === 'mod' ? 'mod' : 'low'}
            onClick={() => go('orient')} icon={r === 'high' ? <Icon.phone size={18} /> : <Icon.book size={18} />}>
            {D.cta}
          </PrimaryButton>
          {r === 'high' && (
            <GhostButton icon={<Icon.phone size={18} />}>Ligar para SAMU 192</GhostButton>
          )}
          <GhostButton onClick={() => go('home')}>Voltar ao início</GhostButton>
        </div>

        <div style={{ marginTop: 14, padding: 12, background: '#EEF4FE', borderRadius: 12, display: 'flex', gap: 10, alignItems: 'flex-start' }}>
          <Icon.info size={16} color={PT.primary} />
          <div style={{ fontSize: 11, color: PT.navy, lineHeight: 1.5, fontWeight: 600 }}>
            Esta orientação é baseada em fluxogramas pediátricos e <b>não substitui</b> a avaliação de um médico.
          </div>
        </div>
      </div>
    </div>
  );
}

function Row({ k, v, last }) {
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-between', padding: '8px 0',
      borderBottom: last ? 'none' : `1px solid ${PT.divider}`,
    }}>
      <div style={{ fontSize: 12, color: PT.textMuted, fontWeight: 600 }}>{k}</div>
      <div style={{ fontSize: 12, color: PT.text, fontWeight: 800 }}>{v}</div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Orientations
// ─────────────────────────────────────────────────────────────
function OrientScreen({ go }) {
  const cards = [
    { id: 1, t: 'O que observar', s: 'Sinais para ficar de olho ao longo do dia', icon: Icon.heart, color: PT.primary, tint: '#EEF4FE' },
    { id: 2, t: 'Quando se preocupar', s: 'Sintomas que pedem atenção rápida', icon: Icon.warn, color: PT.modSolid, tint: PT.modSofter },
    { id: 3, t: 'Cuidados em casa', s: 'Hidratação, repouso e medicação básica', icon: Icon.bed, color: PT.lowSolid, tint: PT.lowSofter },
    { id: 4, t: 'Quando procurar ajuda', s: 'Sinais para ir à emergência sem demora', icon: Icon.phone, color: PT.highSolid, tint: PT.highSofter },
  ];
  return (
    <div style={{ paddingBottom: 120 }}>
      <ScreenHeader title="Orientações" onBack={() => go('home')} />
      <div style={{ padding: '0 20px' }}>
        <div style={{
          background: `linear-gradient(135deg, ${PT.secondary}, ${PT.primary})`,
          borderRadius: 20, padding: 18, color: '#fff', marginBottom: 14, position: 'relative', overflow: 'hidden',
        }}>
          <div style={{ position: 'absolute', right: -20, top: -20, opacity: 0.18 }}>
            <Icon.heart size={120} sw={1.2} />
          </div>
          <div style={{ fontSize: 11, fontWeight: 800, opacity: 0.9, textTransform: 'uppercase', letterSpacing: 0.6 }}>
            Guia para cuidadores
          </div>
          <div style={{ fontSize: 18, fontWeight: 800, marginTop: 6, lineHeight: 1.25 }}>
            Cuidados em casa<br />pela faixa etária
          </div>
          <div style={{ fontSize: 12, fontWeight: 600, opacity: 0.9, marginTop: 8 }}>
            Conteúdo revisado por pediatras.
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {cards.map(c => {
            const I = c.icon;
            return (
              <Card key={c.id} padding={14} style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                <div style={{
                  width: 44, height: 44, borderRadius: 14, background: c.tint, color: c.color,
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                }}><I size={22} /></div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 14, fontWeight: 800, color: PT.text }}>{c.t}</div>
                  <div style={{ fontSize: 11.5, color: PT.textMuted, marginTop: 2, fontWeight: 600 }}>{c.s}</div>
                </div>
                <Icon.chevR size={18} color={PT.textSubtle} />
              </Card>
            );
          })}
        </div>

        <div style={{ marginTop: 18 }}>
          <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 10 }}>
            Por sintoma
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {SYMPTOMS.slice(0, 4).map(s => {
              const I = s.icon;
              return (
                <Card key={s.id} padding={12} style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <div style={{
                    width: 32, height: 32, borderRadius: 10,
                    background: `${s.color}22`, color: s.color,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}><I size={18} /></div>
                  <div style={{ fontSize: 12, fontWeight: 800, color: PT.text }}>{s.name}</div>
                </Card>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// History
// ─────────────────────────────────────────────────────────────
function HistoryScreen({ go }) {
  const [tab, setTab] = React.useState('all');
  const filt = tab === 'all' ? HISTORY : HISTORY.filter(h => h.child === tab);
  return (
    <div style={{ paddingBottom: 120 }}>
      <ScreenHeader title="Histórico" onBack={() => go('home')} />
      <div style={{ padding: '0 20px' }}>
        {/* Mini stats */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8, marginBottom: 14 }}>
          <Stat n="5" l="Avaliações" tone={PT.primary} />
          <Stat n="3" l="Baixo risco" tone={PT.lowSolid} />
          <Stat n="1" l="Alto risco" tone={PT.highSolid} />
        </div>

        {/* Tabs */}
        <div style={{
          display: 'flex', gap: 6, padding: 5, background: '#fff',
          borderRadius: 14, boxShadow: PT.shadow, marginBottom: 14,
        }}>
          {[{ id: 'all', l: 'Todos' }, ...CHILDREN.map(c => ({ id: c.name, l: c.name }))].map(t => (
            <button key={t.id} onClick={() => setTab(t.id)} style={{
              flex: 1, padding: '10px 8px', borderRadius: 10, border: 'none',
              background: tab === t.id ? PT.primary : 'transparent',
              color: tab === t.id ? '#fff' : PT.textMuted,
              fontSize: 12, fontWeight: 800, cursor: 'pointer', fontFamily: PT.font,
            }}>{t.l}</button>
          ))}
        </div>

        {/* Timeline */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          {filt.map(h => <HistoryRow key={h.id} h={h} onClick={() => {}} />)}
        </div>
      </div>
    </div>
  );
}

function Stat({ n, l, tone }) {
  return (
    <div style={{
      background: '#fff', borderRadius: 14, padding: 12, boxShadow: PT.shadow,
      border: `1px solid ${PT.hairline}`, textAlign: 'center',
    }}>
      <div style={{ fontSize: 22, fontWeight: 800, color: tone }}>{n}</div>
      <div style={{ fontSize: 10, color: PT.textMuted, fontWeight: 700, marginTop: 2 }}>{l}</div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Profile
// ─────────────────────────────────────────────────────────────
function ProfileScreen({ go }) {
  return (
    <div style={{ paddingBottom: 120 }}>
      <ScreenHeader title="Perfil" />
      <div style={{ padding: '0 20px' }}>
        <Card padding={16} style={{ display: 'flex', alignItems: 'center', gap: 14, marginBottom: 14 }}>
          <div style={{
            width: 56, height: 56, borderRadius: 18,
            background: `linear-gradient(135deg, ${PT.secondary}, ${PT.primary})`,
            color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 22, fontWeight: 800,
          }}>CR</div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 15, fontWeight: 800, color: PT.text }}>Camila Ribeiro</div>
            <div style={{ fontSize: 12, color: PT.textMuted, fontWeight: 600, marginTop: 2 }}>2 crianças cadastradas</div>
          </div>
          <Icon.chevR size={18} color={PT.textSubtle} />
        </Card>

        <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>
          Crianças
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 16 }}>
          {CHILDREN.map(c => (
            <Card key={c.id} padding={12} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div style={{
                width: 42, height: 42, borderRadius: 14, background: c.tint,
                display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 22,
              }}>{c.emoji}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 800, color: PT.text }}>{c.name}</div>
                <div style={{ fontSize: 11, color: PT.textMuted, fontWeight: 600 }}>{c.age} · {c.weight}</div>
              </div>
              <Icon.chevR size={18} color={PT.textSubtle} />
            </Card>
          ))}
          <button onClick={() => go('child-add')} style={{
            border: `1.5px dashed ${PT.hairline}`, background: 'transparent', cursor: 'pointer',
            borderRadius: 14, padding: 14, color: PT.primary, fontWeight: 800, fontSize: 13,
            fontFamily: PT.font, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
          }}><Icon.plus size={16} /> Adicionar criança</button>
        </div>

        <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>
          Preferências
        </div>
        <div style={{ background: '#fff', borderRadius: 16, boxShadow: PT.shadow, overflow: 'hidden' }}>
          {[
            { i: Icon.bell, t: 'Notificações' },
            { i: Icon.shield, t: 'Privacidade e dados' },
            { i: Icon.info, t: 'Sobre o aplicativo', go: 'about' },
          ].map((row, i, arr) => {
            const I = row.i;
            return (
              <div key={i} onClick={() => row.go && go(row.go)} style={{
                display: 'flex', alignItems: 'center', gap: 12, padding: '14px 16px',
                borderBottom: i < arr.length - 1 ? `1px solid ${PT.divider}` : 'none',
                cursor: 'pointer',
              }}>
                <div style={{ color: PT.primary }}><I size={20} /></div>
                <div style={{ flex: 1, fontSize: 13, fontWeight: 700, color: PT.text }}>{row.t}</div>
                <Icon.chevR size={16} color={PT.textSubtle} />
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// About
// ─────────────────────────────────────────────────────────────
function AboutScreen({ go }) {
  return (
    <div style={{ paddingBottom: 32 }}>
      <ScreenHeader title="Sobre o aplicativo" onBack={() => go('profile')} />
      <div style={{ padding: '0 20px' }}>
        <div style={{
          background: `linear-gradient(135deg, ${PT.navy}, ${PT.primary})`,
          borderRadius: 24, padding: 22, color: '#fff', marginBottom: 16, textAlign: 'center',
        }}>
          <div style={{
            width: 64, height: 64, borderRadius: 20, background: 'rgba(255,255,255,0.18)',
            display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
            marginBottom: 12,
          }}><Icon.heart size={32} sw={2.2} /></div>
          <div style={{ fontSize: 22, fontWeight: 800 }}>PediTriagem</div>
          <div style={{ fontSize: 12, opacity: 0.85, marginTop: 4, fontWeight: 600 }}>Versão 1.0.0</div>
        </div>

        <Card padding={16} style={{ marginBottom: 12 }}>
          <div style={{ fontSize: 14, fontWeight: 800, color: PT.text, marginBottom: 8 }}>
            Apoio à decisão para pais e cuidadores
          </div>
          <div style={{ fontSize: 13, color: PT.textMuted, lineHeight: 1.55, fontWeight: 600 }}>
            O PediTriagem é uma ferramenta digital de orientação inicial, baseada em fluxogramas
            clínicos e protocolos pediátricos amplamente reconhecidos.
          </div>
        </Card>

        <Card padding={16} style={{ marginBottom: 12, background: PT.modSofter, border: `1px solid ${PT.modSolid}33` }}>
          <div style={{ display: 'flex', gap: 10 }}>
            <div style={{ color: PT.modSolid, marginTop: 2 }}><Icon.warn size={20} /></div>
            <div>
              <div style={{ fontSize: 13, fontWeight: 800, color: '#7A5A0E' }}>Não substitui consulta médica</div>
              <div style={{ fontSize: 12, color: '#7A5A0E', marginTop: 4, lineHeight: 1.5, fontWeight: 600 }}>
                Em caso de dúvida, sempre consulte um pediatra. Em emergências, ligue 192 (SAMU)
                ou procure o serviço de saúde mais próximo.
              </div>
            </div>
          </div>
        </Card>

        <div style={{ background: '#fff', borderRadius: 16, boxShadow: PT.shadow, overflow: 'hidden' }}>
          {[
            'Termos de uso',
            'Política de privacidade',
            'Fontes clínicas e referências',
            'Equipe e créditos',
          ].map((t, i, arr) => (
            <div key={t} style={{
              padding: '14px 16px', display: 'flex', justifyContent: 'space-between',
              alignItems: 'center', cursor: 'pointer',
              borderBottom: i < arr.length - 1 ? `1px solid ${PT.divider}` : 'none',
            }}>
              <div style={{ fontSize: 13, fontWeight: 700, color: PT.text }}>{t}</div>
              <Icon.chevR size={16} color={PT.textSubtle} />
            </div>
          ))}
        </div>

        <div style={{ textAlign: 'center', fontSize: 11, color: PT.textSubtle, marginTop: 18, fontWeight: 600 }}>
          © 2026 · Projeto acadêmico · Feito com cuidado
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  FEVER_QUIZ, RISK_DATA,
  QuizScreen, ResultScreen, OrientScreen, HistoryScreen, ProfileScreen, AboutScreen, Stat, Row,
});
